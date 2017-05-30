/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.ldapgroupsync.plugin.impl;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.adsk.jira.ldapgroupsync.plugin.web.LdapGroupSyncConfigMgr;
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
public class LdapGroupSyncLDAPUtils {
    private static final Logger LOGGER = Logger.getLogger(LdapGroupSyncLDAPUtils.class);
    private static LdapGroupSyncLDAPUtils myLdapUtils = null;
    private static Properties properties = null;
    
    public static String LDAP_URL = null;
    public static String SECURITY_PRINCIPAL = null;
    public static String SECURITY_PASSWORD = null;
    public static String BASE_DN = null;    
    public static String GROUP_SEARCH_FILTER = null;
    public static String GROUP_MEMBER_SEARCH_FILTER = null;
    public static String USER_MEMBER_SEARCH_FILTER = null;    
    public static String USER_ATTR = null;
    public static String IS_NESTED = null;
    
    private LdapGroupSyncLDAPUtils() {
        ApplicationProperties props = ComponentAccessor.getApplicationProperties();
        LDAP_URL = props.getString(LdapGroupSyncConfigMgr.LDAP_URL);
        SECURITY_PRINCIPAL = props.getString(LdapGroupSyncConfigMgr.SECURITY_PRINCIPAL);
        SECURITY_PASSWORD = props.getString(LdapGroupSyncConfigMgr.SECURITY_PASSWORD);
        BASE_DN = props.getString(LdapGroupSyncConfigMgr.BASE_DN);
        GROUP_SEARCH_FILTER = props.getString(LdapGroupSyncConfigMgr.GROUP_SEARCH_FILTER);
        USER_MEMBER_SEARCH_FILTER = props.getString(LdapGroupSyncConfigMgr.USER_MEMBER_SEARCH_FILTER);
        GROUP_MEMBER_SEARCH_FILTER = props.getString(LdapGroupSyncConfigMgr.GROUP_MEMBER_SEARCH_FILTER);
        USER_ATTR = props.getString(LdapGroupSyncConfigMgr.USER_ATTR);
        IS_NESTED = props.getString(LdapGroupSyncConfigMgr.IS_NESTED);
        if(GROUP_SEARCH_FILTER == null || "".equals(GROUP_SEARCH_FILTER)) { //default values
            GROUP_SEARCH_FILTER = "(&(objectClass=group)(sAMAccountName={0}))";
        }
        if( USER_MEMBER_SEARCH_FILTER == null || "".equals(USER_MEMBER_SEARCH_FILTER) ) {
            USER_MEMBER_SEARCH_FILTER = "(&(objectClass=user)(memberOf={0}))";
        }
        if( GROUP_MEMBER_SEARCH_FILTER == null || "".equals(GROUP_MEMBER_SEARCH_FILTER) ) {
            GROUP_MEMBER_SEARCH_FILTER = "(&(objectClass=group)(memberOf={0}))";
        }
        if( USER_ATTR == null || "".equals(USER_ATTR) ) {
            USER_ATTR = "sAMAccountName";
        }
        if( IS_NESTED == null || "".equals(IS_NESTED) ) {
            IS_NESTED = "FALSE";
        }
    }
    
    public static LdapGroupSyncLDAPUtils getInstance() {
        if( myLdapUtils == null ) {
            myLdapUtils = new LdapGroupSyncLDAPUtils();
        }
        return myLdapUtils;
    }
    
    private static List<String> getUsersInGroup(LdapContext ctx, String groupName) {
        List<String> users = new ArrayList<String>();
        try {            
            String searchFilter = MessageFormat.format(USER_MEMBER_SEARCH_FILTER, groupName);
            
            NamingEnumeration answer = ctx.search(BASE_DN, searchFilter, getMemberSearchControls());
            while (answer.hasMoreElements()) {
                SearchResult searchResult = (SearchResult) answer.next();                
                Attributes attrs = searchResult.getAttributes();
                if ( attrs != null ) {
                    Attribute attr = (Attribute) attrs.get(USER_ATTR);
                    NamingEnumeration e = attr.getAll();
                    while (e.hasMore()) {
                        String entry = (String) e.next();
                        users.add(entry);                        
                    }
                } else {
                    LOGGER.debug("No members for groups found");
                }
            }
        } catch (NamingException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
        return users;
    }   
    
    public static List<String> getNestedGroups(LdapContext ctx, String groupName) {
        List<String> groups = new ArrayList<String>();
        try {
            if( ctx != null ) {
                String searchFilter = MessageFormat.format(GROUP_MEMBER_SEARCH_FILTER, groupName);
                
                NamingEnumeration answer = ctx.search(BASE_DN, searchFilter, getGroupSearchControls());
                
                while (answer.hasMoreElements()) {
                    SearchResult sr = (SearchResult)answer.next();
                    groups.add(sr.getNameInNamespace());
                }
            }
        } catch (NamingException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
        return groups;
    }
    
    public Set<String> getGroupMembers(String groupName) {        
        Set<String> users = null;
        LdapContext ctx = getLdapContext();
        try {
            if( ctx != null ) {
                String searchFilter = MessageFormat.format(GROUP_SEARCH_FILTER, groupName);
                
                NamingEnumeration answer = ctx.search(BASE_DN, searchFilter, getGroupSearchControls());
                
                if (answer.hasMoreElements()) {
                    SearchResult sr = (SearchResult)answer.next();                
                    LOGGER.debug(">>>" + sr.getNameInNamespace());
                    
                    users = new HashSet<String>(); // create set object
                    
                    List<String> new_users_list = getUsersInGroup(ctx, sr.getNameInNamespace());
                    if(new_users_list.size() > 0) {
                        users.addAll(new_users_list);
                    }
                    
                    // support nested groups
                    if("TRUE".equalsIgnoreCase(IS_NESTED)) {
                        List<String> groups = getNestedGroups(ctx, sr.getNameInNamespace());
                        if(groups.size() > 0) {
                            for(String group : groups) {
                                List<String> nested_user_list = getUsersInGroup(ctx, group);
                                if(nested_user_list.size() > 0) {
                                    users.addAll(nested_user_list);
                                }
                            }
                        }
                    }
                }
            }
        } catch (NamingException e) {
            LOGGER.error(e.getLocalizedMessage());
        } finally {
            try {
                if(ctx != null) ctx.close();
            } catch (NamingException e) {
                LOGGER.error(e.getLocalizedMessage());
            }
        }        
        return users;
    }
    
    private static LdapContext getLdapContext() {
        LdapContext ctx = null;
        if( LDAP_URL != null && !"".equals(LDAP_URL) && SECURITY_PRINCIPAL != null && !"".equals(SECURITY_PRINCIPAL) 
                && SECURITY_PASSWORD != null && !"".equals(SECURITY_PASSWORD) && BASE_DN != null && !"".equals(BASE_DN) 
                && GROUP_SEARCH_FILTER != null && !"".equals(GROUP_SEARCH_FILTER)
                && USER_ATTR != null && !"".equals(USER_ATTR) ) {
            try {
                Properties ldap_settings = getLdapProperties();                
                ctx = new InitialLdapContext(ldap_settings, null);
                LOGGER.debug("LDAP Connection Initialized.");
            } catch (NamingException e) {
                LOGGER.error("LDAP Connection Failed.");
                LOGGER.error(e.getLocalizedMessage());
            }
        }
        return ctx;
    }
    
    private static Properties getLdapProperties() {        
        if( properties == null ) {
            properties = new Properties();
            properties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            properties.put(Context.SECURITY_AUTHENTICATION, "Simple");
            properties.put(Context.PROVIDER_URL, LDAP_URL);
            properties.put(Context.SECURITY_PRINCIPAL, SECURITY_PRINCIPAL);
            properties.put(Context.SECURITY_CREDENTIALS, SECURITY_PASSWORD);
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
    
    private static SearchControls getMemberSearchControls() {
        SearchControls cons = new SearchControls();
        cons.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String[] attrIDs = {USER_ATTR};
        cons.setReturningAttributes(attrIDs);
        cons.setCountLimit(1000); //limit
        return cons;
    }
    
    private static SearchControls getGroupSearchControls() {
        SearchControls cons = new SearchControls();
        cons.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String[] attrIDs = {"cn"};
        cons.setReturningAttributes(attrIDs);
        return cons;
    }
    
    public static void destroyLdapContext() {
        properties = null;
    }
}
