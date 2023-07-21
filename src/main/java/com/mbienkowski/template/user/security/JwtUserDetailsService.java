package com.mbienkowski.template.user.security;

import com.mbienkowski.template.user.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(final String login) throws UsernameNotFoundException {
        var user = userService.find(login);

        return org.springframework.security.core.userdetails.User.builder()
            .username(login)
            .password(user.getPasswordHash())
            .authorities(List.of())
            .build();
    }

}
