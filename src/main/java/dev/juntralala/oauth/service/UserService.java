package dev.juntralala.oauth.service;

import dev.juntralala.oauth.dto.UserPrincipal;
import dev.juntralala.oauth.dto.reuqest.UserRegisterRequest;
import dev.juntralala.oauth.entity.Role;
import dev.juntralala.oauth.entity.User;
import dev.juntralala.oauth.exception.ValidationException;
import dev.juntralala.oauth.repository.UserRepository;
import org.springframework.data.domain.Example;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findFirstByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        return new UserPrincipal(user);
    }

    public User register(UserRegisterRequest registerRequest) throws ValidationException {

        if (!registerRequest.getPassword().matches(registerRequest.getPassword())) {
            throw new ValidationException("Password and repeated password must be match");
        }

        User userProbe = new User();
        userProbe.setUsername(registerRequest.getUsername());
        boolean isUsernameAlreadyExists = userRepository.exists(Example.of(userProbe));
        if (isUsernameAlreadyExists) {
            throw new ValidationException("Username already exists");
        }

        Role role = roleService.getRole("USER");

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setNickname(registerRequest.getNickname());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(role);

        userRepository.save(user);

        return user;
    }

    public void deleteAll() {
        userRepository.deleteAll();
    }
}
