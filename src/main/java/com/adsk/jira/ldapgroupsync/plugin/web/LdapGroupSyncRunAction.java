package com.adsk.jira.ldapgroupsync.plugin.web;

import com.adsk.jira.ldapgroupsync.plugin.api.LDAPGroupSyncUtil;
import com.adsk.jira.ldapgroupsync.plugin.api.LdapGroupSyncAOMgr;
import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.adsk.jira.ldapgroupsync.plugin.model.LdapGroupSyncBean;
import com.adsk.jira.ldapgroupsync.plugin.model.MessageBean;
import java.util.HashSet;
import java.util.Set;
import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;
import org.apache.log4j.Logger;

/**
 * @author scmenthusiast@gmail.com
 */
public class LdapGroupSyncRunAction extends JiraWebActionSupport {
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(LdapGroupSyncRunAction.class);
    private final LdapGroupSyncBean configBean = new LdapGroupSyncBean();    
    private String submitted;
    private String status;
    
    private static Set<String> defaultJiraGroups = null;
    
    private final LdapGroupSyncAOMgr ldapGroupSyncMgr;
    private final LDAPGroupSyncUtil ldapGroupSyncUtil;
    public LdapGroupSyncRunAction(LdapGroupSyncAOMgr ldapGroupAoMgr, LDAPGroupSyncUtil ldapGroupSyncUtil) {
        this.ldapGroupSyncMgr = ldapGroupAoMgr;
        this.ldapGroupSyncUtil = ldapGroupSyncUtil;
        
        defaultJiraGroups = new HashSet<String>();
        defaultJiraGroups.add("jira-users");
        defaultJiraGroups.add("jira-administrators");
        defaultJiraGroups.add("jira-software-users");
        defaultJiraGroups.add("jira-developers");
    }
    
    @Override
    public String doExecute() throws Exception {
        if ( !hasGlobalPermission(GlobalPermissionKey.ADMINISTER) ) {
            return "error";
        }
        
        if (this.submitted != null) {
            String ldapGroup = configBean.getLdapGroup();
            String jiraGroup = configBean.getJiraGroup();
            
            logger.debug("Runing Sync -> "+ ldapGroup + " : "+ jiraGroup);
            
            if(ldapGroup != null && !"".equals(ldapGroup) && jiraGroup != null && !"".equals(jiraGroup)) {
                if(!defaultJiraGroups.contains(jiraGroup.toLowerCase()) ) {
                    if(ldapGroupSyncMgr.isJiraGroupNotInSupport(jiraGroup) == true) {
                        status = "This JIRA group ("+jiraGroup+") is configured not to support. Skipping!";
                    }else{
                        status = "Running.";
                        
                        LdapContext ctx = null;        
                        try {
                            long startTime = System.currentTimeMillis();
                            
                            ctx = ldapGroupSyncUtil.getLdapContext();
                            
                            MessageBean result = ldapGroupSyncUtil.sync(ctx, ldapGroup.trim(), jiraGroup.trim());

                            long totalTime = System.currentTimeMillis() - startTime;                
                            logger.info(result.getMessage() +". Took "+ totalTime/ 1000d +" Seconds");

                            result.setMessage(result.getMessage() +". Took "+ totalTime/ 1000d +" Seconds");
                            status = result.getMessage();
                        
                        } finally {
                            try {
                                if(ctx != null) ctx.close();
                            } catch (NamingException e) {
                                logger.error(e);
                            }
                        }
                    }
                    
                } else {
                    status = "This plugin does not support JIRA default group ("+jiraGroup+"). Skipping!";
                }
                
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
