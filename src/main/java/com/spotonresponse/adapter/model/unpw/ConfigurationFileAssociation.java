package com.spotonresponse.adapter.model.unpw;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ConfigurationFileAssociation {
    @Id
    private String username;

    private String password;

    private String configName;

    public ConfigurationFileAssociation(String username, String password, String configName) {
        this.username = username;
        this.password = password;
        this.configName = configName;
    }

    public ConfigurationFileAssociation() {}

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof com.spotonresponse.adapter.model.unpw.ConfigurationFileAssociation))
            return false;
        com.spotonresponse.adapter.model.unpw.ConfigurationFileAssociation other = (com.spotonresponse.adapter.model.unpw.ConfigurationFileAssociation)o;
        if (!other.canEqual(this))
            return false;
        Object this$username = getUsername(), other$username = other.getUsername();
        if ((this$username == null) ? (other$username != null) : !this$username.equals(other$username))
            return false;
        Object this$password = getPassword(), other$password = other.getPassword();
        if ((this$password == null) ? (other$password != null) : !this$password.equals(other$password))
            return false;
        Object this$configName = getConfigName(), other$configName = other.getConfigName();
        return !((this$configName == null) ? (other$configName != null) : !this$configName.equals(other$configName));
    }

    protected boolean canEqual(Object other) {
        return other instanceof com.spotonresponse.adapter.model.unpw.ConfigurationFileAssociation;
    }

    public int hashCode() {
        int PRIME = 59;
       int  result = 1;
        Object $username = getUsername();
        result = result * 59 + (($username == null) ? 43 : $username.hashCode());
        Object $password = getPassword();
        result = result * 59 + (($password == null) ? 43 : $password.hashCode());
        Object $configName = getConfigName();
        return result * 59 + (($configName == null) ? 43 : $configName.hashCode());
    }

    public String toString() {
        return "ConfigurationFileAssociation(username=" + getUsername() + ", password=" + getPassword() + ", configName=" + getConfigName() + ")";
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getConfigName() {
        return this.configName;
    }
}