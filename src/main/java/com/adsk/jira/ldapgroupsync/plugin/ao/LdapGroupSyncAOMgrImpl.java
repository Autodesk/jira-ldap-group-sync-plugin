/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.ldapgroupsync.plugin.ao;

import com.atlassian.activeobjects.external.ActiveObjects;
import net.java.ao.Query;

/**
 *
 * @author prasadve
 */
public class LdapGroupSyncAOMgrImpl implements LdapGroupSyncAOMgr {
    private final ActiveObjects ao;
    public LdapGroupSyncAOMgrImpl(ActiveObjects ao) {
        this.ao = ao;
    }
    public ActiveObjects getActiveObjects() {
        return this.ao;
    }
    @Override
    public LdapGroupSyncMap[] getSupportedGroupsMapProperties() {
        return ao.find(LdapGroupSyncMap.class, Query.select().where("SUPPORT = ?", true));
    }
    @Override
    public boolean isJiraGroupNotInSupport(String jiraGroup) {
        final LdapGroupSyncMap[] maps = ao.find(LdapGroupSyncMap.class, Query.select()
                .where("JIRA_GROUP = ? AND SUPPORT = ?", jiraGroup, false));
        return maps.length > 0;
    }
}
