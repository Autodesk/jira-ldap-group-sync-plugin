package com.adsk.jira.ldapgroupsync.plugin.config;

import com.atlassian.jira.permission.GlobalPermissionKey;
import com.adsk.jira.ldapgroupsync.plugin.svc.MyLdapGroupSyncDAO;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.adsk.jira.ldapgroupsync.plugin.model.LdapGroupSyncBean;
import com.adsk.jira.ldapgroupsync.plugin.model.MessageBean;
import org.apache.log4j.Logger;

/**
 * @author scmenthusiast@gmail.com
 */
public class LdapGroupSyncRunAction extends JiraWebActionSupport {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(LdapGroupSyncRunAction.class);
    private final LdapGroupSyncBean configBean = new LdapGroupSyncBean();
    private String submitted;
    private String status;
    
    @Override
    public String doExecute() throws Exception {
        if ( !hasGlobalPermission(GlobalPermissionKey.ADMINISTER) ) {
            return "error";
        }
        if (this.submitted == null) {            
            //configBean = ldapGroupSyncMgr.getGroupsRunProperties();             
        } else {
            String ldapGroup = configBean.getLdapGroup();
            String jiraGroup = configBean.getJiraGroup();
            LOGGER.debug("Runing Sync -> "+ ldapGroup + " : "+ jiraGroup);
            if(ldapGroup != null && !"".equals(ldapGroup) && jiraGroup != null && !"".equals(jiraGroup)) {
                status = "Running.";
                long startTime = System.currentTimeMillis();
                
                MessageBean result = MyLdapGroupSyncDAO.getInstance().sync(ldapGroup.trim(), jiraGroup.trim());
                
                long totalTime = System.currentTimeMillis() - startTime;                
                LOGGER.info(result.getMessage() +". Took "+ totalTime/ 1000d +" Seconds");
                
                result.setMessage(result.getMessage() +". Took "+ totalTime/ 1000d +" Seconds");
                status = result.getMessage();
            } else {
                status = "Failed. Required fields are missing!";
            }
        }
        return "success";
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
    
    public void setSubmitted(String submitted) {
        this.submitted = submitted;
    }
    
    public String getStatus() {
        return status;
    }
}
