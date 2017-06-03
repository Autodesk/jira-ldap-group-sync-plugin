package com.adsk.jira.ldapgroupsync.plugin.web;

import com.adsk.jira.ldapgroupsync.plugin.impl.LdapGroupSyncAOMgr;
import com.adsk.jira.ldapgroupsync.plugin.impl.LdapGroupSyncAOMgrImpl;
import com.adsk.jira.ldapgroupsync.plugin.model.LdapGroupSyncMapBean;
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
    private final LdapGroupSyncAOMgr ldapGroupAoMgr = LdapGroupSyncAOMgrImpl.getInstance();    
    private final LdapGroupSyncMapBean configBean = new LdapGroupSyncMapBean();
    private String submitted;
    private String status;
        
    @Override
    public String doExecute() throws Exception {
        if ( !hasGlobalPermission(GlobalPermissionKey.ADMINISTER) ) {
            return "error";
        }
        if (this.submitted != null && "ADD".equals(this.submitted)) {            
            LOGGER.debug("Adding groups map -> "+ configBean.getLdapGroup() +":"+configBean.getJiraGroup()+":"+ configBean.isSupport());
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
        else if (this.submitted != null && "SAVE".equals(this.submitted)) {            
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
        else if (this.submitted != null && "DEL".equals(this.submitted)) {
            LOGGER.debug("Deleting groups Config map Id -> "+ configBean.getConfigId());           
            if(configBean.getConfigId() > 0) {
                ldapGroupAoMgr.removeGroupsMapProperty(configBean.getConfigId());
                status = "Deleted.";
            }else{
                status = "Config Config Id "+configBean.getConfigId()+" is not accepted!";
            }
        }
        else {
            if(configBean.getConfigId() > 0) {
                LdapGroupSyncMapBean bean = ldapGroupAoMgr
                        .getGroupsMapProperty(configBean.getConfigId());
                configBean.setConfigId(bean.getConfigId());
                configBean.setJiraGroup(bean.getJiraGroup());
                configBean.setLdapGroup(bean.getLdapGroup());
                configBean.setSupport(bean.isSupport());
            }
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
    
    public boolean isSupport() {
        return configBean.isSupport();
    }

    public void setSupport(boolean support) {
        configBean.setSupport(support);
    }
    
    public List<LdapGroupSyncMapBean> getConfigList() {
        return ldapGroupAoMgr.getAllGroupsMapProperties();
    }
    
    public void setSubmitted(String submitted) {
        this.submitted = submitted;
    }
    
    public String getStatus() {
        return status;
    }
}
