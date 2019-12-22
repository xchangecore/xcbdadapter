package com.spotonresponse.adapter.security.unpw;

import com.spotonresponse.adapter.model.unpw.ConfigurationFileAssociation;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class ConfigUserDetails implements UserDetails {
    private final ConfigurationFileAssociation configurationFileAssociation;

    public ConfigUserDetails(ConfigurationFileAssociation configurationFileAssociation) {
        this.configurationFileAssociation = configurationFileAssociation;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.NO_AUTHORITIES;
    }

    @Override
    public String getPassword() {
        return this.configurationFileAssociation.getPassword();
    }

    @Override
    public String getUsername() {
        return this.configurationFileAssociation.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
