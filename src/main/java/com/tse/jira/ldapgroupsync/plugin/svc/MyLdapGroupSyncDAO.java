package com.tse.jira.ldapgroupsync.plugin.svc;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.google.gson.Gson;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import com.tse.jira.ldapgroupsync.plugin.config.LdapGroupSyncMapMgr;
import com.tse.jira.ldapgroupsync.plugin.model.MessageBean;
import java.util.HashSet;
import java.util.Set;

public class MyLdapGroupSyncDAO
{
    private static final Logger LOGGER = Logger.getLogger(MyLdapGroupSyncDAO.class);    
    private static MyLdapGroupSyncDAO myLdapGroupSyncDAO = null;    
    private static Set<String> defaultJiraGroups = null;
    public static String ldapGroupSyncMap = null;
    
    private MyLdapGroupSyncDAO() {
        ApplicationProperties props = ComponentAccessor.getApplicationProperties();
        ldapGroupSyncMap = props.getString(LdapGroupSyncMapMgr.GROUPS_MAP);
        if( ldapGroupSyncMap == null || "".equals(ldapGroupSyncMap) ) ldapGroupSyncMap = "{}"; //default
        //default jira groups to skip
        defaultJiraGroups = new HashSet<String>();
        defaultJiraGroups.add("jira-users");        
        defaultJiraGroups.add("jira-administrators");        
        defaultJiraGroups.add("jira-software-users");
        defaultJiraGroups.add("jira-developers");
    }
    
    public static MyLdapGroupSyncDAO getInstance() {
        if( myLdapGroupSyncDAO == null ) {
            myLdapGroupSyncDAO = new MyLdapGroupSyncDAO();
        }
        return myLdapGroupSyncDAO;
    }
    
    private static Map getGsonMap(String maps) {
        Gson g = new Gson();        
        return g.fromJson(maps, Map.class);
    }
    
    public void run() {
        Map groups_map = getGsonMap(ldapGroupSyncMap);
        for(Object key : groups_map.keySet()) {
            String ldap_group = key.toString();
            String jira_group = groups_map.get(key).toString();            
            MessageBean message = sync(ldap_group, jira_group); //Do Sync Here.
            LOGGER.debug(message.getMessage());
        }
    }
    
    public MessageBean sync(String ldap_group, String jira_group) {
        long startTime = System.currentTimeMillis();
        long totalTime = 0;
        
        LOGGER.debug(" >>> "+ ldap_group +" : "+ jira_group +" <<< ");
        MessageBean message = new MessageBean();
        
        if( defaultJiraGroups.contains(jira_group.toLowerCase()) ) {
            message.setMessage("This plugin does not support JIRA default group ("+jira_group+"). Skipping!");
            message.setStatus(1);
            return message;
        }
        
        List<String> ldap_group_users = MyLdapUtils.getInstance().getGroupMembers(ldap_group);                
        if( ldap_group_users == null ) {
            LOGGER.warn("LDAP Group ("+ldap_group+") does not exists.");
            message.setMessage("LDAP Group ("+ldap_group+") does not exists.");
            message.setStatus(1);
            
        } else {
            
            LOGGER.debug(" >>> Size: "+ ldap_group_users.size());
            
            List<String> jira_group_users = MyJiraUtils.getInstance().getGroupMembers(jira_group);
            if( jira_group_users != null ) {            
                for(String j : jira_group_users) {
                    if(!ldap_group_users.contains(j)) {
                        MyJiraUtils.getInstance().removeUserFromGroup(j, jira_group);
                    }
                }
                for(String i : ldap_group_users) {
                    if(!jira_group_users.contains(i)) {
                        MyJiraUtils.getInstance().addUserToGroup(i, jira_group);
                    }
                }
            } else {
                LOGGER.debug("JIRA Group members return NULL. So adding LDAP users.");
                for(String i : ldap_group_users) {
                    MyJiraUtils.getInstance().addUserToGroup(i, jira_group);
                }
            }
            
            totalTime = System.currentTimeMillis() - startTime;
            message.setMessage("Successful. Size("+ldap_group_users.size()+"). Took " + totalTime/ 1000d +" Seconds");
            message.setStatus(0);
        
        }
        totalTime = System.currentTimeMillis() - startTime;
        LOGGER.info(totalTime/ 1000d +" Seconds");
        
        return message;
    }
}