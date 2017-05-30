package com.adsk.jira.ldapgroupsync.plugin.api;

import com.adsk.jira.ldapgroupsync.plugin.impl.LdapGroupSyncDAO;
import com.adsk.jira.ldapgroupsync.plugin.model.MessageBean;
import com.atlassian.configurable.ObjectConfiguration;
import com.atlassian.configurable.ObjectConfigurationException;
import com.atlassian.jira.service.AbstractService;
import org.apache.log4j.Logger;
import com.adsk.jira.ldapgroupsync.plugin.impl.LdapGroupSyncAOMgr;
import com.adsk.jira.ldapgroupsync.plugin.impl.LdapGroupSyncAOMgrImpl;
import com.adsk.jira.ldapgroupsync.plugin.model.LdapGroupSyncMapBean;
import java.util.List;

public class LdapGroupSync extends AbstractService
{
    private static final Logger LOGGER = Logger.getLogger(LdapGroupSync.class);    
    private final LdapGroupSyncAOMgr ao;
    private LdapGroupSync() {
        ao = LdapGroupSyncAOMgrImpl.getInstance();
    }
    
    @Override
    public void run() {
        long startTime = System.currentTimeMillis();        
        LOGGER.info("LDAP Group(s) Sync Service Started.");        
        
        List<LdapGroupSyncMapBean> maps = ao.getSupportedGroupsMapProperties();
        for(LdapGroupSyncMapBean m : maps) {
            MessageBean message = LdapGroupSyncDAO.getInstance()
                    .sync(m.getLdapGroup(), m.getJiraGroup()); //Do Sync Here.
            LOGGER.debug(message.getMessage());
        }
        
        long totalTime = System.currentTimeMillis() - startTime;
        LOGGER.info("Service Finished. Took "+ totalTime/ 1000d +" Seconds");
    }
    
    @Override
    public ObjectConfiguration getObjectConfiguration() throws ObjectConfigurationException {
        return getObjectConfiguration("AsdkMyLdapSyncService0001", "jira-ldap-group-sync-plugin.xml", null);
    }
    
}