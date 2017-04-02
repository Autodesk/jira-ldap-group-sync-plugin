package com.adsk.jira.ldapgroupsync.plugin.config;

import com.adsk.jira.ldapgroupsync.plugin.ao.LdapGroupSyncMap;
import com.adsk.jira.ldapgroupsync.plugin.model.LdapGroupSyncBean;
import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import java.text.MessageFormat;
import org.apache.log4j.Logger;

/**
 * @author scmenthusiast@gmail.com
 */
public class LdapGroupSyncMapAction extends JiraWebActionSupport {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(LdapGroupSyncMapAction.class);
    private final LdapGroupSyncMapMgr ldapGroupSyncMgr;    
    private final LdapGroupSyncBean configBean = new LdapGroupSyncBean();    
    private String submitted;
    private String mapId;
    private String status;
    
    public LdapGroupSyncMapAction(LdapGroupSyncMapMgr ldapGroupSyncMgr) {
        this.ldapGroupSyncMgr = ldapGroupSyncMgr;
    }
        
    @Override
    public String doExecute() throws Exception {
        if ( !hasGlobalPermission(GlobalPermissionKey.ADMINISTER) ) {
            return "error";
        }
        if (this.submitted != null && "ADD".equals(this.submitted)) {            
            LOGGER.debug("Saving groups map -> "+ configBean.getLdapGroup() +":"+configBean.getJiraGroup());
            if(ldapGroupSyncMgr.findGroupsMapProperty(configBean) == false) {
                if(configBean.getLdapGroup() != null && !"".equals(configBean.getLdapGroup()) 
                        && configBean.getJiraGroup() !=null && !"".equals(configBean.getJiraGroup())) {
                    ldapGroupSyncMgr.setGroupsMapProperty(configBean);
                    status = "Saved.";
                }else{
                    status = "Ldap/Jira Group field missing!";
                }
            }else{
                status = MessageFormat.format("{0}/{1} Group alredy exists in mapping!",
                        configBean.getLdapGroup(), configBean.getJiraGroup());
            }
        }
        if (this.submitted != null && "DEL".equals(this.submitted)) {
            LOGGER.debug("Deleting groups map Id -> "+ mapId);
            try {
                long id = Long.parseLong(mapId);
                if(id > 0) {
                    ldapGroupSyncMgr.removeGroupsMapProperty(id);
                    status = "Deleted.";
                }else{
                    status = "MapId "+id+" is not accepted!";
                }
            }catch (NumberFormatException e) {
                LOGGER.error("Map ID can be only Number");
                status = "Map ID can be only Number!";
            }            
        }
        return "success";
    }
    
    public String getMapId() {
        return mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
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
    
    public LdapGroupSyncMap[] getMapsList() {
        return ldapGroupSyncMgr.getGroupsMapProperties();
    }
    
    public void setSubmitted(String submitted) {
        this.submitted = submitted;
    }
    
    public String getStatus() {
        return status;
    }
}
