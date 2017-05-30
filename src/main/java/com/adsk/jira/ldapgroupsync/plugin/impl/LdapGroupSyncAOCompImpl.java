/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.ldapgroupsync.plugin.impl;

import com.atlassian.activeobjects.external.ActiveObjects;

/**
 *
 * @author prasadve
 */
public class LdapGroupSyncAOCompImpl implements LdapGroupSyncAOComp {
    private final ActiveObjects ao;
    public LdapGroupSyncAOCompImpl(ActiveObjects ao) {
        this.ao = ao;
    }
    public ActiveObjects getActiveObjects() {
        return this.ao;
    }
}
