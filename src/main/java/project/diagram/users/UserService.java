package project.diagram.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.diagram.security.model.User;
import project.diagram.security.model.UserPrincipal;
import project.diagram.security.user.UserInfo;
import project.diagram.security.service.AbstractOAuthUserService;
import project.diagram.security.payload.SignupRequest;
import java.util.Optional;

@Service
public class UserService extends AbstractOAuthUserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    public void oauthSignupCallback(UserInfo userInfo) {

    }

    @Override
    public void regularSignupCallback(SignupRequest signUpRequest) {

    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        User user = findByEmail(email).orElse(null);
        return user != null;
    }

    @Override
    public void save(User user) {
        repository.save(user);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User User = repository.findByEmail(email).orElse(null);

        if (User == null)
            throw new UsernameNotFoundException("User not found with email : " + email);

        return UserPrincipal.create(User);
    }
}
