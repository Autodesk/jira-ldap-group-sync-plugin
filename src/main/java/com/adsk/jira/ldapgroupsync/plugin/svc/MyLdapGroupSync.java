package com.adsk.jira.ldapgroupsync.plugin.svc;

import com.atlassian.configurable.ObjectConfiguration;
import com.atlassian.configurable.ObjectConfigurationException;
import com.atlassian.jira.service.AbstractService;
import com.opensymphony.module.propertyset.PropertySet;
import org.apache.log4j.Logger;

public class MyLdapGroupSync extends AbstractService
{
    private static final Logger LOGGER = Logger.getLogger(MyLdapGroupSync.class);
    
    // Map strings for listener properties
    public static final String IS_ACTIVE = "is_active";
    private boolean is_active = false;
    
    @Override
    public void init(PropertySet props) throws ObjectConfigurationException {
        super.init(props);
        if (hasProperty(IS_ACTIVE)){
            String active_name = getProperty(IS_ACTIVE);
            is_active = Boolean.parseBoolean(active_name);
        }
    }
    
    @Override
    public void run() {
        long startTime = System.currentTimeMillis();        
        LOGGER.info("LDAP Group(s) Sync Service Started.");
        
        if(is_active == true) {
            MyLdapGroupSyncDAO.getInstance().run();
        }
        
        long totalTime = System.currentTimeMillis() - startTime;
        LOGGER.info("Service Finished. Took "+ totalTime/ 1000d +" Seconds");
    }
    
    @Override
    public void destroy() {
        super.destroy();
    }
    
    @Override
    public ObjectConfiguration getObjectConfiguration() throws ObjectConfigurationException {
        return getObjectConfiguration("AsdkMyLdapSyncService0001", "jira-ldap-group-sync-plugin.xml", null);
    }
    
}