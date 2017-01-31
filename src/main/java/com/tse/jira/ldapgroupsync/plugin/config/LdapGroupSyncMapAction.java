package com.tse.jira.ldapgroupsync.plugin.config;

import com.atlassian.jira.permission.GlobalPermissionKey;
import com.tse.jira.ldapgroupsync.plugin.model.LdapGroupSyncMapBean;
import com.tse.jira.ldapgroupsync.plugin.svc.MyLdapGroupSyncDAO;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * @author scmenthusiast@gmail.com
 */
public class LdapGroupSyncMapAction extends JiraWebActionSupport {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(LdapGroupSyncMapAction.class);
    private final LdapGroupSyncMapMgr ldapGroupSyncMgr;
    private LdapGroupSyncMapBean configBean = new LdapGroupSyncMapBean();
    private String submitted;
    private String status;
    
    public LdapGroupSyncMapAction(LdapGroupSyncMapMgr ldapGroupSyncMgr) {
        this.ldapGroupSyncMgr = ldapGroupSyncMgr;
    }
        
    @Override
    public String doExecute() throws Exception {
        if ( !hasGlobalPermission(GlobalPermissionKey.ADMINISTER) ) {
            return "error";
        }
        if (this.submitted == null) {            
            configBean = ldapGroupSyncMgr.getGroupsMapProperties();            
        } else {
            LOGGER.debug("Saving groups map -> "+ configBean.getGroups_map());            
            try {
                if(configBean.getGroups_map() != null && !"".equals(configBean.getGroups_map())) {
                    Gson g = new Gson();        
                    Map map = g.fromJson(configBean.getGroups_map(), Map.class);
                    if( map instanceof Map ) {
                        ldapGroupSyncMgr.setGroupsMapProperties(configBean);
                        MyLdapGroupSyncDAO.ldapGroupSyncMap = configBean.getGroups_map();
                        status = "Saved.";
                    }
                }else{
                    ldapGroupSyncMgr.setGroupsMapProperties(configBean);
                    MyLdapGroupSyncDAO.ldapGroupSyncMap = "{}";
                    status = "Saved.";
                }
            } catch (JsonSyntaxException e) {
                status = "Failed. "+e.getLocalizedMessage();
            }            
        }
        return "success";
    }

    public String getGroups_map() {
        return configBean.getGroups_map();
    }
    
    public void setGroups_map(String groups_map) {
        configBean.setGroups_map(groups_map);
    }    
    
    public void setSubmitted(String submitted) {
        this.submitted = submitted;
    }
    
    public String getStatus() {
        return status;
    }
}
