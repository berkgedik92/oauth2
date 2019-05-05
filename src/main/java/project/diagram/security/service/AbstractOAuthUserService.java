package project.diagram.security.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import project.diagram.security.model.User;
import project.diagram.security.payload.SignupRequest;
import project.diagram.security.user.UserInfo;

import java.util.Optional;

public abstract class AbstractOAuthUserService implements UserDetailsService {
    public abstract void oauthSignupCallback(UserInfo userInfo);
    public abstract void regularSignupCallback(SignupRequest signUpRequest);
    public abstract Optional<User> findByEmail(String email);
    public abstract boolean existsByEmail(String email);
    public abstract void save(User user);
}
