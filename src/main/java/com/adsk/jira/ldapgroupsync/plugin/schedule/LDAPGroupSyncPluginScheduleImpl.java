/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.ldapgroupsync.plugin.schedule;

import com.adsk.jira.ldapgroupsync.plugin.api.LDAPGroupSyncUtil;
import com.adsk.jira.ldapgroupsync.plugin.api.LdapGroupSyncAOMgr;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.scheduling.PluginScheduler;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

/**
 *
 * @author prasadve
 */
public class LDAPGroupSyncPluginScheduleImpl implements LDAPGroupSyncPluginSchedule, LifecycleAware {
    private static final Logger logger = Logger.getLogger(LDAPGroupSyncPluginScheduleImpl.class);
    
    private Date lastRun = null;
    
    private final ApplicationProperties applicationProperties;
    private final PluginScheduler pluginScheduler;  // provided by SAL    
    private final LdapGroupSyncAOMgr ldapGroupSyncMgr;
    private final LDAPGroupSyncUtil ldapGroupSyncUtil;
    
    public LDAPGroupSyncPluginScheduleImpl(ApplicationProperties applicationProperties, 
            PluginScheduler pluginScheduler, LdapGroupSyncAOMgr ldapGroupAoMgr, 
            LDAPGroupSyncUtil ldapGroupSyncUtil) {
        this.applicationProperties = applicationProperties;
        this.pluginScheduler = pluginScheduler;
        this.ldapGroupSyncMgr = ldapGroupAoMgr;
        this.ldapGroupSyncUtil = ldapGroupSyncUtil;
    }
    
    public long getInterval() {
        long interval = 1L;
        try {
            long sync_interval = Long.parseLong(applicationProperties
                    .getString(LDAPGroupSyncPluginSchedule.SYNC_INTERVAL));
            if(sync_interval > 0) {
                interval = sync_interval;
            }else{
                interval = LDAPGroupSyncPluginSchedule.DEFAULT_INTERVAL;
            }
        } catch (NumberFormatException e) {
            logger.debug("LDAP Group Sync interval property is null so using default: "+ 
                    LDAPGroupSyncPluginSchedule.DEFAULT_INTERVAL);
            
            interval = LDAPGroupSyncPluginSchedule.DEFAULT_INTERVAL;
        }
        return interval;
    }
    
    public Date getLastRun() {
        return this.lastRun;
    }
    
    public Date getNextRun() {
        Calendar cal = Calendar.getInstance();
        if(lastRun != null) {
            cal.setTime(lastRun);
        }
        cal.add(Calendar.MILLISECOND, (int) TimeUnit.HOURS.toMillis(getInterval()));
        return cal.getTime();
    }

    public void onStart() {
        /**
         * Important place to Change minutes or hours
         * conversion here.
         */
        long time_interval = TimeUnit.HOURS.toMillis(getInterval());
                
        pluginScheduler.scheduleJob(JOB_NAME,                   // unique name of the job
                LDAPGroupSyncPluginTask.class,            // class of the job
                new HashMap<String,Object>() {{
                    put(LDAPGroupSyncPluginSchedule.KEY, LDAPGroupSyncPluginScheduleImpl.this);
                }},                    // data that needs to be passed to the job
                getNextRun(),          // the time the job is to start
                time_interval);             // interval between repeats, in milliseconds
        logger.debug(String.format("LDAP Groups Sync task scheduled to run every %dhrs", getInterval()));
    }

    public void onStop() {
        this.pluginScheduler.unscheduleJob(JOB_NAME);
    }
    
    public void reschedule() {
        onStop();
        onStart();
    }

    public void setLastRun(Date lastRun) {
        this.lastRun = lastRun;
    }
    
    public LDAPGroupSyncUtil getLDAPGroupSyncUtil() {
        return ldapGroupSyncUtil;
    }
    
    public LdapGroupSyncAOMgr getLdapGroupSyncAOMgr() {
        return ldapGroupSyncMgr;
    }
}
