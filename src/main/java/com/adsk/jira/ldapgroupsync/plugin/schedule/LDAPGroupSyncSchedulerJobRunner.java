/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.ldapgroupsync.plugin.schedule;

import com.atlassian.scheduler.JobRunner;
import java.util.Date;

/**
 * @author prasadve
 */
public interface LDAPGroupSyncSchedulerJobRunner extends JobRunner {
    public static final String SYNC_INTERVAL = "com.adsk.jira.ldapgroupsync.plugin.sync_interval";    
    public static final String KEY = LDAPGroupSyncPluginSchedule.class.getName() + ":instance";    
    public static final String JOB_NAME = LDAPGroupSyncPluginSchedule.class.getName() + ":job";    
    public static final long DEFAULT_INTERVAL = 24L;
    public long getInterval();
    public void setLastRun(Date lastRun);
    public Date getLastRun();
}
