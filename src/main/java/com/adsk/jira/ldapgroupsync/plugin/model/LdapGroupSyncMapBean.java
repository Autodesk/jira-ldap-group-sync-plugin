package com.adsk.jira.ldapgroupsync.plugin.model;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect
public class LdapGroupSyncMapBean {
    @JsonProperty
    private long configId;
    @JsonProperty
    private String ldapGroup;
    @JsonProperty
    private String jiraGroup;

    public long getConfigId() {
        return configId;
    }

    public void setConfigId(long configId) {
        this.configId = configId;
    }        
    
    public String getLdapGroup() {
        return ldapGroup;
    }

    public void setLdapGroup(String ldapGroup) {
        this.ldapGroup = ldapGroup;
    }

    public String getJiraGroup() {
        return jiraGroup;
    }

    public void setJiraGroup(String jiraGroup) {
        this.jiraGroup = jiraGroup;
    }
    
    public void clear() {
        this.ldapGroup = null;
        this.jiraGroup=null;
    }
}
