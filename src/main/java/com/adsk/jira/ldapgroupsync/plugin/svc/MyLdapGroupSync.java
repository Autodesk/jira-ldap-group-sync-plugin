package com.adsk.jira.ldapgroupsync.plugin.svc;

import com.adsk.jira.ldapgroupsync.plugin.ao.LdapGroupSyncMap;
import com.adsk.jira.ldapgroupsync.plugin.model.MessageBean;
import com.atlassian.configurable.ObjectConfiguration;
import com.atlassian.configurable.ObjectConfigurationException;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.service.AbstractService;
import org.apache.log4j.Logger;
import com.adsk.jira.ldapgroupsync.plugin.ao.LdapGroupSyncAOMgr;

public class MyLdapGroupSync extends AbstractService
{
    private static final Logger LOGGER = Logger.getLogger(MyLdapGroupSync.class);    
    private final LdapGroupSyncAOMgr ao;
    public MyLdapGroupSync() {
        ao = ComponentAccessor.getOSGiComponentInstanceOfType(LdapGroupSyncAOMgr.class);
    }
    
    @Override
    public void run() {
        long startTime = System.currentTimeMillis();        
        LOGGER.info("LDAP Group(s) Sync Service Started.");        
        
        LdapGroupSyncMap[] maps = ao.getSupportedGroupsMapProperties();
        for(LdapGroupSyncMap m : maps) {
            MessageBean message = MyLdapGroupSyncDAO.getInstance()
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