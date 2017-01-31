/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tse.jira.ldapgroupsync.plugin.svc;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.tse.jira.ldapgroupsync.plugin.config.LdapGroupSyncConfigMgr;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
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
public class MyLdapUtils {
    private static final Logger LOGGER = Logger.getLogger(MyLdapUtils.class);
    private static MyLdapUtils myLdapUtils = null;
    private static Properties properties = null;
    
    public static String LDAP_URL = null;
    public static String SECURITY_PRINCIPAL = null;
    public static String SECURITY_PASSWORD = null;
    public static String BASE_DN = null;
    public static String SEARCH_FILTER = null;
    public static String USER_ATTR = null;
    public static String MEMBER_ATTR = null;
    
    private MyLdapUtils() {
        ApplicationProperties props = ComponentAccessor.getApplicationProperties();
        LDAP_URL = props.getString(LdapGroupSyncConfigMgr.LDAP_URL);
        SECURITY_PRINCIPAL = props.getString(LdapGroupSyncConfigMgr.SECURITY_PRINCIPAL);
        SECURITY_PASSWORD = props.getString(LdapGroupSyncConfigMgr.SECURITY_PASSWORD);
        BASE_DN = props.getString(LdapGroupSyncConfigMgr.BASE_DN);
        SEARCH_FILTER = props.getString(LdapGroupSyncConfigMgr.SEARCH_FILTER);
        MEMBER_ATTR = props.getString(LdapGroupSyncConfigMgr.MEMBER_ATTR);
        USER_ATTR = props.getString(LdapGroupSyncConfigMgr.USER_ATTR);
        if( SEARCH_FILTER == null || "".equals(SEARCH_FILTER) ) { //default values
            SEARCH_FILTER = "(&(objectClass=group)(sAMAccountName={0}))";
        }
        if( MEMBER_ATTR == null || "".equals(MEMBER_ATTR) ) {
            MEMBER_ATTR = "member";
        }
        if( USER_ATTR == null || "".equals(USER_ATTR) ) {
            USER_ATTR = "sAMAccountName";
        }
    }
    
    public static MyLdapUtils getInstance() {
        if( myLdapUtils == null ) {
            myLdapUtils = new MyLdapUtils();
        }
        return myLdapUtils;
    }
    
    public List<String> getGroupMembers(String groupName) {
        List<String> users = null;
        LdapContext ctx = getLdapContext();
        try {
            if( ctx != null ) {                
                //specify the LDAP search filter
                String searchFilter = MessageFormat.format(SEARCH_FILTER, groupName);
                //Search for objects using the filter
                NamingEnumeration answer = ctx.search(BASE_DN, searchFilter, getGroupSearchControls());
                if (answer.hasMoreElements()) {
                    SearchResult sr = (SearchResult)answer.next();                
                    LOGGER.debug(">>>" + sr.getName());
                    
                    //Print out the members
                    Attributes attrs = sr.getAttributes();
                    if ( attrs != null ) {
                        users = new ArrayList<String>();
                        try {
                            Attribute attr = (Attribute) attrs.get(MEMBER_ATTR);
                            for ( NamingEnumeration e = attr.getAll(); e.hasMore(); ) {
                                String entry = (String) e.next();
                                LOGGER.debug(" = " +  entry);
                                String[] entryNames = entry.split(",");
                                String userName = getUserInfo(entryNames[0], ctx, getUserSearchControls());
                                if( userName != null ) users.add(userName);
                                //LOGGER.debug(" = " +  userName);
                            }                       
                        } catch (NamingException e)     {
                            LOGGER.error(e.getLocalizedMessage());
                        }
                    } else {
                        LOGGER.warn("No members for groups found");
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
                && SEARCH_FILTER != null && !"".equals(SEARCH_FILTER) && MEMBER_ATTR != null && !"".equals(MEMBER_ATTR)
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
    
    private static String getUserInfo(String userName, LdapContext ctx, SearchControls searchControls) {
        String username = null;
        try {
            NamingEnumeration<SearchResult> answer = ctx.search(BASE_DN, userName, searchControls);
            if (answer.hasMore()) {
                Attributes attrs = answer.next().getAttributes();
                if( attrs.get(USER_ATTR) != null ) {
                    username = attrs.get(USER_ATTR).get().toString();
                }
            } else {
                LOGGER.warn(userName+" not found.");
            }
        } catch (NamingException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
        return username;
    }
    
    private static SearchControls getUserSearchControls() {
        SearchControls cons = new SearchControls();
        cons.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String[] attrIDs = {USER_ATTR};
        cons.setReturningAttributes(attrIDs);
        return cons;
    }
    
    private static SearchControls getGroupSearchControls() {
        SearchControls cons = new SearchControls();
        cons.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String[] attrIDs = {MEMBER_ATTR};
        cons.setReturningAttributes(attrIDs);
        return cons;
    }
    
    public static void destroyLdapContext() {
        properties = null;
    }
}
