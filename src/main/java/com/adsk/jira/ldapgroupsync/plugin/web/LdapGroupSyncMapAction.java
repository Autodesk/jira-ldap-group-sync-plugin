package com.adsk.jira.ldapgroupsync.plugin.web;

import com.adsk.jira.ldapgroupsync.plugin.api.LdapGroupSyncAOMgr;
import com.adsk.jira.ldapgroupsync.plugin.model.LdapGroupSyncMapBean;
import com.adsk.jira.ldapgroupsync.plugin.schedule.LDAPGroupSyncPluginSchedule;
import com.adsk.jira.ldapgroupsync.plugin.schedule.LDAPGroupSyncSchedulerJobRunner;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import java.text.MessageFormat;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * @author scmenthusiast@gmail.com
 */
public class LdapGroupSyncMapAction extends JiraWebActionSupport {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(LdapGroupSyncMapAction.class);    
    private final LdapGroupSyncMapBean configBean = new LdapGroupSyncMapBean();
    private long interval;
    private String submitted;
    private String status;
    
    private final LdapGroupSyncAOMgr ldapGroupAoMgr;
    private final LDAPGroupSyncPluginSchedule pluginSchedule;
    private final ApplicationProperties properties;
    
    public LdapGroupSyncMapAction(LdapGroupSyncAOMgr ldapGroupAoMgr, 
            LDAPGroupSyncPluginSchedule pluginSchedule, ApplicationProperties properties) {
        this.ldapGroupAoMgr = ldapGroupAoMgr;
        this.pluginSchedule = pluginSchedule;
        this.properties = properties;
    }
    
    @Override
    public String doExecute() throws Exception {
        if ( !hasGlobalPermission(GlobalPermissionKey.ADMINISTER) ) {
            return "error";
        }
        if (this.submitted != null && "ADD".equals(this.submitted)) {            
            LOGGER.debug("Adding groups map -> "+ configBean.getLdapGroup() +":"+configBean.getJiraGroup());
            if(ldapGroupAoMgr.findGroupsMapProperty(configBean) == false) {
                if(configBean.getLdapGroup() != null && !"".equals(configBean.getLdapGroup()) 
                        && configBean.getJiraGroup() !=null && !"".equals(configBean.getJiraGroup())) {
                    ldapGroupAoMgr.addGroupsMapProperty(configBean);
                    configBean.clear();
                    status = "Saved.";
                }else{
                    status = "Ldap/Jira Group field missing!";
                }
            }else{
                status = MessageFormat.format("{0} or {1} group already exists in mapping!",
                        configBean.getLdapGroup(), configBean.getJiraGroup());
            }
        }
        else if (this.submitted != null && "Save".equals(this.submitted)) {            
            LOGGER.debug("Saving groups map -> "+ configBean.getLdapGroup() +":"+
                    configBean.getJiraGroup()+":"+ configBean.getConfigId());
            if(ldapGroupAoMgr.findGroupsMapProperty2(configBean) == false) {
                if(configBean.getLdapGroup() != null && !"".equals(configBean.getLdapGroup()) 
                        && configBean.getJiraGroup() !=null && !"".equals(configBean.getJiraGroup())) {
                    ldapGroupAoMgr.setGroupsMapProperty(configBean);
                    configBean.clear();
                    status = "Saved.";
                }else{
                    status = "Ldap/Jira Group field missing!";
                }
            }else{
                status = MessageFormat.format("{0} or {1} group already exists in mapping!",
                        configBean.getLdapGroup(), configBean.getJiraGroup());
            }
        }
        else if (this.submitted != null && "DELETE".equals(this.submitted)) {
            LOGGER.debug("Deleting groups Config map Id -> "+ configBean.getConfigId());           
            if(configBean.getConfigId() > 0) {
                ldapGroupAoMgr.removeGroupsMapProperty(configBean.getConfigId());
                status = "Deleted.";
            }else{
                status = "Config Config Id "+configBean.getConfigId()+" is not accepted!";
            }
        }
        else if (this.submitted != null && "Schedule".equals(this.submitted)) {
            LOGGER.debug("Re-Scheduling Sync with interval -> "+ interval);           
            if(interval >= 0) {
                properties.setString(LDAPGroupSyncSchedulerJobRunner.SYNC_INTERVAL, ""+interval);
                status = "Re-scheduled Sync with interval: "+ interval;
                pluginSchedule.reschedule();
            }
        }
        else {
            interval = pluginSchedule.getInterval();
        }
        return "success";
    }
    
    public long getConfigId() {
        return configBean.getConfigId();
    }

    public void setConfigId(long configId) {
        configBean.setConfigId(configId);
    }
    
    public String getLdapGroup() {
        return configBean.getLdapGroup();
    }

    public void setLdapGroup(String ldapGroup) {
        configBean.setLdapGroup(ldapGroup);
    }

    public String getJiraGroup() {
        return configBean.getJiraGroup();
    }

    public void setJiraGroup(String jiraGroup) {
        configBean.setJiraGroup(jiraGroup);
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }        
    
    public List<LdapGroupSyncMapBean> getConfigList() {
        return ldapGroupAoMgr.getGroupsMapProperties();
    }
    
    public void setSubmitted(String submitted) {
        this.submitted = submitted;
    }
    
    public String getStatus() {
        return status;
    }
}
