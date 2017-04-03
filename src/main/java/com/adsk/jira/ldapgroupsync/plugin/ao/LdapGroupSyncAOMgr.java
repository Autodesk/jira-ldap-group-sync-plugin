/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.ldapgroupsync.plugin.ao;

import com.atlassian.activeobjects.external.ActiveObjects;

/**
 *
 * @author prasadve
 */
public interface LdapGroupSyncAOMgr {
    public ActiveObjects getActiveObjects();
    public LdapGroupSyncMap[] getSupportedGroupsMapProperties();
    public boolean isJiraGroupNotInSupport(String jiraGroup);
}
