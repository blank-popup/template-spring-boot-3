package com.example.template.setting.security;

import com.example.template.setting.redis.ServiceRedis;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ServiceUserDetails implements UserDetailsService {
    private final ServiceRedis serviceRedis;

    @Override
    public UserDetails loadUserByUsername(String token) throws UsernameNotFoundException {
        AhaUserDetails userDetails = serviceRedis.getAuthTokenUser(token);
        if (userDetails == null) {
            throw new UsernameNotFoundException("Not found UserDetails : " + token);
        }

        return userDetails;
    }
}
