/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.ldapgroupsync.plugin.schedule;

import com.adsk.jira.ldapgroupsync.plugin.api.LDAPGroupSyncUtil;
import com.adsk.jira.ldapgroupsync.plugin.api.LdapGroupSyncAOMgr;
import java.util.Date;

/**
 *
 * @author prasadve
 */
public interface LDAPGroupSyncPluginSchedule {
    public static final String SYNC_INTERVAL = "com.adsk.jira.ldapgroupsync.plugin.sync_interval";
    
    public static final String KEY = LDAPGroupSyncPluginSchedule.class.getName() + ":instance";
    
    public static final String JOB_NAME = LDAPGroupSyncPluginSchedule.class.getName() + ":job";
    
    public static final long DEFAULT_INTERVAL = 24L;
    
    public LdapGroupSyncAOMgr getLdapGroupSyncAOMgr();
    
    public LDAPGroupSyncUtil getLDAPGroupSyncUtil();
    
    public long getInterval();    
    public void reschedule();
    public void setLastRun(Date lastRun);
    public Date getLastRun();
    public Date getNextRun();
}
