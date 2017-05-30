/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.ldapgroupsync.plugin.impl;

import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.exception.OperationNotPermittedException;
import com.atlassian.crowd.exception.embedded.InvalidGroupException;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.exception.AddException;
import com.atlassian.jira.exception.PermissionException;
import com.atlassian.jira.exception.RemoveException;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.user.util.UserUtil;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author prasadve
 */
public class LdapGroupSyncJiraUtils {
    private static final Logger LOGGER = Logger.getLogger(LdapGroupSyncJiraUtils.class);
    private static LdapGroupSyncJiraUtils myJiraUtils = null;        
    private final GroupManager groupManager = ComponentAccessor.getGroupManager();
    private final UserManager userManager = ComponentAccessor.getUserManager();
    private final UserUtil userUtil = ComponentAccessor.getUserUtil();
    private LdapGroupSyncJiraUtils() {}
    
    public static LdapGroupSyncJiraUtils getInstance() {
        if( myJiraUtils == null ) {
            myJiraUtils = new LdapGroupSyncJiraUtils();
        }
        return myJiraUtils;
    }
    
    public List<String> getGroupMembers(String groupName) {
        List<String> users = null;
        if( groupManager.groupExists(groupName) ) {
            users = new ArrayList<String>();
            for(String i : groupManager.getUserNamesInGroup(groupName)) {
                users.add(i);
            }
        }
        return users;
    }
        
    public void removeUserFromGroup(String userName, String groupName) {                
        if( groupManager.groupExists(groupName) ) {            
            try {
                ApplicationUser appUser = userManager.getUserByName(userName);
                if( appUser != null ) {
                    Group group = groupManager.getGroup(groupName);
                    userUtil.removeUserFromGroup(group, appUser);
                    LOGGER.info("Removed Jira user ("+userName+") from group ("+groupName+")");
                }
            } catch (PermissionException ex) {
                LOGGER.error(ex.getLocalizedMessage());
            } catch (RemoveException ex) {
                LOGGER.error(ex.getLocalizedMessage());
            }
        }
    }
    
    public void addUserToGroup(String userName, String groupName) {
        if( !groupManager.groupExists(groupName) ) createGroup(groupName);
        if( groupManager.groupExists(groupName) ) {
            try {
                ApplicationUser appUser = userManager.getUserByName(userName);
                if( appUser != null ) {
                    Group group = groupManager.getGroup(groupName);
                    userUtil.addUserToGroup(group, appUser);
                    LOGGER.info("Added Jira user ("+userName+") to group ("+groupName+")");
                } else {
                    LOGGER.info("Jira User ("+userName+") does not exists.");
                }            
            } catch (PermissionException ex) {
                LOGGER.error(ex.getLocalizedMessage());
            } catch (AddException ex) {
                LOGGER.error(ex.getLocalizedMessage());
            }
        }
    }
    
    private void createGroup(String groupName) {        
        try {            
            groupManager.createGroup(groupName);
            LOGGER.info("New Jira group ("+groupName+") created");
        } catch (OperationNotPermittedException ex) {
            LOGGER.error(ex.getLocalizedMessage());
        } catch (InvalidGroupException ex) {
            LOGGER.error(ex.getLocalizedMessage());
        }
    }
}
