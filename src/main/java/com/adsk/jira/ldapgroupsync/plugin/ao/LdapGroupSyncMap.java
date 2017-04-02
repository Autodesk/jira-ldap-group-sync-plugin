/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.ldapgroupsync.plugin.ao;


import net.java.ao.Entity;
import net.java.ao.Preload;

/**
 *
 * @author prasadve
 */
@Preload
public interface LdapGroupSyncMap extends Entity {
    public String getLdapGroup();
    public void setLdapGroup(String ldapGroup);    
    public String getJiraGroup();
    public void setJiraGroup(String jiraGroup);
}
