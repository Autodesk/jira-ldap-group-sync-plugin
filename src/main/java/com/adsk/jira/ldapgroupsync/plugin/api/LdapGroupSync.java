package com.adsk.jira.ldapgroupsync.plugin.api;

import com.atlassian.configurable.ObjectConfiguration;
import com.atlassian.configurable.ObjectConfigurationException;
import com.atlassian.jira.service.AbstractService;
import org.apache.log4j.Logger;

public class LdapGroupSync extends AbstractService
{
    private static final Logger LOGGER = Logger.getLogger(LdapGroupSync.class);
    
    @Override
    public void run() {
        long startTime = System.currentTimeMillis();        
        LOGGER.info("LDAP Group(s) Sync Service Started.");        
        
        /*List<LdapGroupSyncMapBean> maps = LdapGroupSyncAOMgrImpl
                .getInstance().getSupportedGroupsMapProperties();
        
        for(LdapGroupSyncMapBean map : maps) {
            MessageBean message = LdapGroupSyncDAO.getInstance()
                    .sync(map.getLdapGroup(), map.getJiraGroup()); //Do Sync Here.
            LOGGER.debug(message.getMessage());
        }*/
        
        long totalTime = System.currentTimeMillis() - startTime;
        LOGGER.info("Service Finished. Took "+ totalTime/ 1000d +" Seconds");
    }
    
    @Override
    public ObjectConfiguration getObjectConfiguration() throws ObjectConfigurationException {
        return getObjectConfiguration("AsdkMyLdapSyncService0001", "jira-ldap-group-sync-plugin.xml", null);
    }
    
}