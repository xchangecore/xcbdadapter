package com.spotonresponse.adapter.security.unpw;

import com.spotonresponse.adapter.repo.unpw.ConfigurationFileAssociationDynamoDBRepository;
import com.spotonresponse.adapter.repo.unpw.ConfigurationFileAssociationRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ConfigUserDetailsService implements UserDetailsService {
    @Autowired
    private ConfigurationFileAssociationDynamoDBRepository configurationFileAssociationRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(username == null){
            throw new UsernameNotFoundException("No such user");
        }

        try {
            val targetUser = configurationFileAssociationRepository.findById(username);

            return new ConfigUserDetails(targetUser);
        } catch (Exception e){
            throw new UsernameNotFoundException("No such user");
        }
    }
}
