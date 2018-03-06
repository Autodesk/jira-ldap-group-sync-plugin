/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.ldapgroupsync.plugin.schedule;

import com.adsk.jira.ldapgroupsync.plugin.api.LDAPGroupSyncUtil;
import com.adsk.jira.ldapgroupsync.plugin.api.LdapGroupSyncAOMgr;
import com.adsk.jira.ldapgroupsync.plugin.model.LdapGroupSyncMapBean;
import com.adsk.jira.ldapgroupsync.plugin.model.MessageBean;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import java.util.Date;
import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;
import org.apache.log4j.Logger;

/**
 *
 * @author prasadve
 */
public class LDAPGroupSyncSchedulerJobRunnerImpl implements LDAPGroupSyncSchedulerJobRunner {
    private static final Logger logger = Logger.getLogger(LDAPGroupSyncSchedulerJobRunnerImpl.class);
    
    private Date lastRun = null;
    private final ApplicationProperties applicationProperties;
    private final LdapGroupSyncAOMgr ldapGroupSyncMgr;
    private final LDAPGroupSyncUtil ldapGroupSyncUtil;
    
    public LDAPGroupSyncSchedulerJobRunnerImpl(ApplicationProperties applicationProperties, 
            LdapGroupSyncAOMgr ldapGroupSyncMgr, final LDAPGroupSyncUtil ldapGroupSyncUtil) {
        this.applicationProperties = applicationProperties;
        this.ldapGroupSyncMgr = ldapGroupSyncMgr;
        this.ldapGroupSyncUtil = ldapGroupSyncUtil;
    }
    
    public long getInterval() {
        long interval = 1L;
        try {
            long sync_interval = Long.parseLong(applicationProperties
                    .getString(SYNC_INTERVAL));
            if(sync_interval > 0) {
                interval = sync_interval;
            }else{
                interval = DEFAULT_INTERVAL;
            }
        } catch (NumberFormatException e) {
            logger.debug("Action Reminders interval property is null so using default: "+ 
                    DEFAULT_INTERVAL);
            
            interval = DEFAULT_INTERVAL;
        }
        return interval;
    }
    
    public Date getLastRun() {
        return this.lastRun;
    }
    
    public void setLastRun(Date lastRun) {
        this.lastRun = lastRun;
    }
    
    public JobRunnerResponse runJob(JobRunnerRequest request) {
        //Date last_run_date_time = getLastRun();        
        //setLastRun(new Date());
        
        /**
         * JRASERVER-29896
         */
        final Thread currentThread = Thread.currentThread();
        final ClassLoader origCCL = currentThread.getContextClassLoader();        
        currentThread.setContextClassLoader(LDAPGroupSyncSchedulerJobRunnerImpl.class.getClass().getClassLoader());
        
        
        LdapContext ctx = null;
        
        try {               
            ctx = ldapGroupSyncUtil.getLdapContext();
                        
            for(LdapGroupSyncMapBean bean : ldapGroupSyncMgr.getGroupsMapProperties()) {
                MessageBean message = ldapGroupSyncUtil.sync(ctx, bean.getLdapGroup(), bean.getJiraGroup());
                logger.debug(message.getMessage());
            }
            
        } finally {
            try {
                if(ctx != null) ctx.close();
            } catch (NamingException e) {
                logger.error(e);
            }           
        }
        
        currentThread.setContextClassLoader(origCCL); //JRASERVER-29896
        
        return JobRunnerResponse.success();
    }
}
