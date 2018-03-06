/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.ldapgroupsync.plugin.api;

import com.adsk.jira.ldapgroupsync.plugin.model.MessageBean;
import com.adsk.jira.ldapgroupsync.plugin.web.LdapGroupSyncConfigMgr;
import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.exception.OperationNotPermittedException;
import com.atlassian.crowd.exception.embedded.InvalidGroupException;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.exception.AddException;
import com.atlassian.jira.exception.PermissionException;
import com.atlassian.jira.exception.RemoveException;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.user.util.UserUtil;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import org.apache.log4j.Logger;

/**
 *
 * @author prasadve
 */
public class LDAPGroupSyncUtilImpl implements LDAPGroupSyncUtil {
    private static final Logger logger = Logger.getLogger(LDAPGroupSyncUtilImpl.class);    
    private static Properties properties = null;    
    
    public static String ldapUrl;
    public static String securityPrincipal;
    public static String securityPassword;
    public static String baseDn;
    public static String GroupSearchFilter;
    public static String GroupMemberSearchFilter;
    public static String UserMemberSearchFilter;    
    public static String UserAttr;
    public static String IsNested;
    
    private static Set<String> defaultJiraGroups = null;
    private final ApplicationProperties props;
    private final GroupManager groupManager;
    private final UserManager userManager;
    private final UserUtil userUtil;
    
    public LDAPGroupSyncUtilImpl(ApplicationProperties props, GroupManager groupManager, 
            UserManager userManager, UserUtil userUtil) {
        
        this.props = props;
        this.groupManager = groupManager;
        this.userManager = userManager;
        this.userUtil = userUtil;
        
        defaultJiraGroups = new HashSet<String>();
        defaultJiraGroups.add("jira-users");
        defaultJiraGroups.add("jira-administrators");
        defaultJiraGroups.add("jira-software-users");
        defaultJiraGroups.add("jira-developers");
        
        ldapUrl = props.getString(LdapGroupSyncConfigMgr.LDAP_URL);
        securityPrincipal = props.getString(LdapGroupSyncConfigMgr.SECURITY_PRINCIPAL);
        securityPassword = props.getString(LdapGroupSyncConfigMgr.SECURITY_PASSWORD);
        baseDn = props.getString(LdapGroupSyncConfigMgr.BASE_DN);
        GroupSearchFilter = props.getString(LdapGroupSyncConfigMgr.GROUP_SEARCH_FILTER);
        UserMemberSearchFilter = props.getString(LdapGroupSyncConfigMgr.USER_MEMBER_SEARCH_FILTER);
        GroupMemberSearchFilter = props.getString(LdapGroupSyncConfigMgr.GROUP_MEMBER_SEARCH_FILTER);
        UserAttr = props.getString(LdapGroupSyncConfigMgr.USER_ATTR);
        IsNested = props.getString(LdapGroupSyncConfigMgr.IS_NESTED);
        if(GroupSearchFilter == null || "".equals(GroupSearchFilter)) { //default values
            GroupSearchFilter = "(&(objectClass=group)(sAMAccountName={0}))";
        }
        if( UserMemberSearchFilter == null || "".equals(UserMemberSearchFilter) ) {
            UserMemberSearchFilter = "(&(objectClass=user)(memberOf={0}))";
        }
        if(GroupMemberSearchFilter == null || "".equals(GroupMemberSearchFilter) ) {
            GroupMemberSearchFilter = "(&(objectClass=group)(memberOf={0}))";
        }
        if( UserAttr == null || "".equals(UserAttr) ) {
            UserAttr = "sAMAccountName";
        }
        if( IsNested == null || "".equals(IsNested) ) {
            IsNested = "FALSE";
        }
    }
    
    public List<String> getUsersInLdapGroup(LdapContext ctx, String groupName) {
        List<String> users = new ArrayList<String>();
        try {            
            String searchFilter = MessageFormat.format(UserMemberSearchFilter, groupName);
            
            NamingEnumeration answer = ctx.search(baseDn, searchFilter, getMemberSearchControls());
            while (answer.hasMoreElements()) {
                SearchResult searchResult = (SearchResult) answer.next();                
                Attributes attrs = searchResult.getAttributes();
                if ( attrs != null ) {
                    Attribute attr = (Attribute) attrs.get(UserAttr);
                    NamingEnumeration e = attr.getAll();
                    while (e.hasMore()) {
                        String entry = (String) e.next();
                        users.add(entry);                        
                    }
                } else {
                    logger.debug("No members for groups found");
                }
            }
        } catch (NamingException e) {
            logger.error(e);
        }
        return users;
    }   
    
    public List<String> getNestedLdapGroups(LdapContext ctx, String groupName) {
        List<String> groups = new ArrayList<String>();
        try {
            if( ctx != null ) {
                String searchFilter = MessageFormat.format(GroupMemberSearchFilter, groupName);
                
                NamingEnumeration answer = ctx.search(baseDn, searchFilter, getGroupSearchControls());
                
                while (answer.hasMoreElements()) {
                    SearchResult sr = (SearchResult)answer.next();
                    groups.add(sr.getNameInNamespace());
                }
            }
        } catch (NamingException e) {
            logger.error(e);
        }
        return groups;
    }
    
    public Set<String> getLdapGroupMembers(LdapContext ctx, String groupName) {        
        Set<String> users = null;
        try {
            if( ctx != null ) {
                String searchFilter = MessageFormat.format(GroupSearchFilter, groupName);
                
                NamingEnumeration answer = ctx.search(baseDn, searchFilter, getGroupSearchControls());
                
                if (answer.hasMoreElements()) {
                    SearchResult sr = (SearchResult)answer.next();                
                    logger.debug(">>>" + sr.getNameInNamespace());
                    
                    users = new HashSet<String>(); // create set object
                    
                    List<String> new_users_list = getUsersInLdapGroup(ctx, sr.getNameInNamespace());
                    if(new_users_list.size() > 0) {
                        users.addAll(new_users_list);
                    }
                    
                    // support nested groups
                    if("TRUE".equalsIgnoreCase(IsNested)) {
                        List<String> groups = getNestedLdapGroups(ctx, sr.getNameInNamespace());
                        if(groups.size() > 0) {
                            for(String group : groups) {
                                List<String> nested_user_list = getUsersInLdapGroup(ctx, group);
                                if(nested_user_list.size() > 0) {
                                    users.addAll(nested_user_list);
                                }
                            }
                        }
                    }
                }
            } else {
                logger.error("LDAP Connection Null or Failed.");
            }
        } catch (NamingException e) {
            logger.error(e);
        }        
        return users;
    }
    
    public LdapContext getLdapContext() {
        LdapContext ctx = null;
        if( ldapUrl != null && !"".equals(ldapUrl) && securityPrincipal != null && !"".equals(securityPrincipal) 
                && securityPassword != null && !"".equals(securityPassword) && baseDn != null && !"".equals(baseDn) 
                && GroupSearchFilter != null && !"".equals(GroupSearchFilter)
                && UserAttr != null && !"".equals(UserAttr) ) {
            try {
                Properties ldap_settings = getLdapProperties();                
                ctx = new InitialLdapContext(ldap_settings, null);
                logger.debug("LDAP Connection Initialized.");
            } catch (NamingException e) {
                logger.error("LDAP Connection Failed.");
                logger.error(e);
            }
        }
        return ctx;
    }
    
    public Properties getLdapProperties() {        
        if( properties == null ) {
            properties = new Properties();
            properties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            properties.put(Context.SECURITY_AUTHENTICATION, "Simple");
            properties.put(Context.PROVIDER_URL, ldapUrl);
            properties.put(Context.SECURITY_PRINCIPAL, securityPrincipal);
            properties.put(Context.SECURITY_CREDENTIALS, securityPassword);
            properties.put(Context.REFERRAL, "ignore");
            properties.put("com.sun.jndi.ldap.connect.pool", "true");
            properties.put("com.sun.jndi.ldap.connect.pool.timeout", "15000");
            properties.put("com.sun.jndi.ldap.connect.pool.maxsize", "20");
            properties.put("com.sun.jndi.ldap.connect.pool.prefsize", "10");
            properties.put("com.sun.jndi.ldap.connect.timeout", "300000");
            properties.put("com.sun.jndi.ldap.read.timeout", "15000");
        }
        return properties;
    }        
    
    public SearchControls getMemberSearchControls() {
        SearchControls cons = new SearchControls();
        cons.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String[] attrIDs = {UserAttr};
        cons.setReturningAttributes(attrIDs);
        cons.setCountLimit(1000); //limit
        return cons;
    }
    
    public SearchControls getGroupSearchControls() {
        SearchControls cons = new SearchControls();
        cons.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String[] attrIDs = {"cn"};
        cons.setReturningAttributes(attrIDs);
        return cons;
    }
    
    public void destroyLdapContext() {
        properties = null;
    }
    
    public List<String> getJiraGroupMembers(String groupName) {
        List<String> users = null;
        if( groupManager.groupExists(groupName) ) {
            users = new ArrayList<String>();
            for(String i : groupManager.getUserNamesInGroup(groupName)) {
                users.add(i);
            }
        }
        return users;
    }
        
    public void removeUserFromJiraGroup(String userName, String groupName) {                
        if( groupManager.groupExists(groupName) ) {            
            try {
                ApplicationUser appUser = userManager.getUserByName(userName);
                if( appUser != null ) {
                    Group group = groupManager.getGroup(groupName);
                    userUtil.removeUserFromGroup(group, appUser);
                    logger.debug("Removed Jira user ("+userName+") from group ("+groupName+")");
                }
            } catch (PermissionException ex) {
                logger.error(ex);
            } catch (RemoveException ex) {
                logger.error(ex);
            }
        }
    }
    
    public void addUserToJiraGroup(String userName, String groupName) {
        if( !groupManager.groupExists(groupName) ) createJiraGroup(groupName);
        if( groupManager.groupExists(groupName) ) {
            try {
                ApplicationUser appUser = userManager.getUserByName(userName);
                if( appUser != null ) {
                    Group group = groupManager.getGroup(groupName);
                    userUtil.addUserToGroup(group, appUser);
                    logger.debug("Added Jira user ("+userName+") to group ("+groupName+")");
                } else {
                    logger.debug("Jira User ("+userName+") does not exists.");
                }            
            } catch (PermissionException ex) {
                logger.error(ex);
            } catch (AddException ex) {
                logger.error(ex);
            }
        }
    }
    
    public void createJiraGroup(String groupName) {        
        try {            
            groupManager.createGroup(groupName);
            logger.debug("New Jira group ("+groupName+") created");
        } catch (OperationNotPermittedException ex) {
            logger.error(ex);
        } catch (InvalidGroupException ex) {
            logger.error(ex);
        }
    }
    
    public MessageBean sync(LdapContext ctx, String ldap_group, String jira_group) {
        
        logger.debug(" >>> "+ ldap_group +" : "+ jira_group +" <<< ");
        MessageBean message = new MessageBean();
        
        if( defaultJiraGroups.contains(jira_group.toLowerCase()) ) {
            message.setMessage("This plugin does not support JIRA default group ("+jira_group+"). Skipping!");
            message.setStatus(1);
            return message;
        }
        
        Set<String> ldap_group_users = getLdapGroupMembers(ctx, ldap_group);                
        if( ldap_group_users == null ) {
            logger.debug("LDAP Group ("+ldap_group+") does not exists.");
            message.setMessage("LDAP Group ("+ldap_group+") does not exists.");
            message.setStatus(1);
            
        } else {
            
            logger.debug(" >>> Size: "+ ldap_group_users.size());
            
            List<String> jira_group_users = getJiraGroupMembers(jira_group);
            if( jira_group_users != null ) {            
                for(String j : jira_group_users) {
                    if(!ldap_group_users.contains(j)) {
                        removeUserFromJiraGroup(j, jira_group);
                    }
                }
                for(String i : ldap_group_users) {
                    if(!jira_group_users.contains(i)) {
                        addUserToJiraGroup(i, jira_group);
                    }
                }
            } else {
                logger.debug("JIRA Group members return NULL. So adding LDAP users.");
                for(String i : ldap_group_users) {
                    addUserToJiraGroup(i, jira_group);
                }
            }
                        
            message.setMessage("Successful. Fetch Size("+ldap_group_users.size()+")");
            message.setStatus(0);
        
        }
        
        return message;
    }

    public void setLdapUrl(String ldapUrl) {
        LDAPGroupSyncUtilImpl.ldapUrl = ldapUrl;
    }

    public void setSecurityPrincipal(String securityPrincipal) {
        LDAPGroupSyncUtilImpl.securityPrincipal = securityPrincipal;
    }

    public void setSecurityPassword(String securityPassword) {
        LDAPGroupSyncUtilImpl.securityPassword = securityPassword;
    }

    public void setBaseDn(String baseDn) {
        LDAPGroupSyncUtilImpl.baseDn = baseDn;
    }

    public void setGroupSearchFilter(String GroupSearchFilter) {
        LDAPGroupSyncUtilImpl.GroupSearchFilter = GroupSearchFilter;
    }

    public void setGroupMemberSearchFilter(String GroupMemberSearchFilter) {
        LDAPGroupSyncUtilImpl.GroupMemberSearchFilter = GroupMemberSearchFilter;
    }

    public void setUserMemberSearchFilter(String UserMemberSearchFilter) {
        LDAPGroupSyncUtilImpl.UserMemberSearchFilter = UserMemberSearchFilter;
    }

    public void setUserAttr(String UserAttr) {
        LDAPGroupSyncUtilImpl.UserAttr = UserAttr;
    }

    public void setIsNested(String IsNested) {
        LDAPGroupSyncUtilImpl.IsNested = IsNested;
    }    
}
