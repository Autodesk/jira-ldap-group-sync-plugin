package com.adsk.jira.ldapgroupsync.plugin.impl;

import java.util.List;
import org.apache.log4j.Logger;
import com.adsk.jira.ldapgroupsync.plugin.model.MessageBean;
import java.util.HashSet;
import java.util.Set;

public class LdapGroupSyncDAO
{
    private static final Logger LOGGER = Logger.getLogger(LdapGroupSyncDAO.class);
    private static LdapGroupSyncDAO myLdapGroupSyncDAO = null;
    private static Set<String> defaultJiraGroups = null;
    public static String ldapGroupSyncMap = null;
    
    private LdapGroupSyncDAO() { //default jira groups to skip        
        defaultJiraGroups = new HashSet<String>();
        defaultJiraGroups.add("jira-users");
        defaultJiraGroups.add("jira-administrators");
        defaultJiraGroups.add("jira-software-users");
        defaultJiraGroups.add("jira-developers");
    }
    
    public static LdapGroupSyncDAO getInstance() {
        if( myLdapGroupSyncDAO == null ) {
            myLdapGroupSyncDAO = new LdapGroupSyncDAO();
        }
        return myLdapGroupSyncDAO;
    }        
    
    public MessageBean sync(String ldap_group, String jira_group) {
        
        LOGGER.debug(" >>> "+ ldap_group +" : "+ jira_group +" <<< ");
        MessageBean message = new MessageBean();
        
        if( defaultJiraGroups.contains(jira_group.toLowerCase()) ) {
            message.setMessage("This plugin does not support JIRA default group ("+jira_group+"). Skipping!");
            message.setStatus(1);
            return message;
        }
        
        Set<String> ldap_group_users = LdapGroupSyncLDAPUtils.getInstance().getGroupMembers(ldap_group);                
        if( ldap_group_users == null ) {
            LOGGER.warn("LDAP Group ("+ldap_group+") does not exists.");
            message.setMessage("LDAP Group ("+ldap_group+") does not exists.");
            message.setStatus(1);
            
        } else {
            
            LOGGER.debug(" >>> Size: "+ ldap_group_users.size());
            
            List<String> jira_group_users = LdapGroupSyncJiraUtils.getInstance().getGroupMembers(jira_group);
            if( jira_group_users != null ) {            
                for(String j : jira_group_users) {
                    if(!ldap_group_users.contains(j)) {
                        LdapGroupSyncJiraUtils.getInstance().removeUserFromGroup(j, jira_group);
                    }
                }
                for(String i : ldap_group_users) {
                    if(!jira_group_users.contains(i)) {
                        LdapGroupSyncJiraUtils.getInstance().addUserToGroup(i, jira_group);
                    }
                }
            } else {
                LOGGER.debug("JIRA Group members return NULL. So adding LDAP users.");
                for(String i : ldap_group_users) {
                    LdapGroupSyncJiraUtils.getInstance().addUserToGroup(i, jira_group);
                }
            }
                        
            message.setMessage("Successful. Fetch Size("+ldap_group_users.size()+")");
            message.setStatus(0);
        
        }
        
        return message;
    }
}