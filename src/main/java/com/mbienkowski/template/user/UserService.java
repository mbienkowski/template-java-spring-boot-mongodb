package com.mbienkowski.template.user;

import com.mbienkowski.template.user.device.Device;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${security.login.uuid.salt}")
    private String uuidSalt;

    public User find(String login) {
        var userId = getUserId(login);
        var user = userRepository.findById(userId);

        return user.orElseThrow(() -> new UsernameNotFoundException(login));
    }

    public User create(String login, String password, List<UserRole> roles, List<Device> devices) {
        var user = User.builder()
            .id(getUserId(login))
            .login(login)
            .passwordHash(passwordEncoder.encode(password))
            .roles(roles)
            .devices(devices)
            .build();
        return userRepository.save(user);
    }

    public User save(User user) {
        user.setId(getUserId(user.getLogin()));
        return userRepository.save(user);
    }

    public boolean isExisting(String login) {
        var userId = getUserId(login);
        var user = userRepository.findById(userId);

        return user.isPresent();
    }

    UUID getUserId(String login){
        return UUID.nameUUIDFromBytes((login + uuidSalt).getBytes());
    }

}
