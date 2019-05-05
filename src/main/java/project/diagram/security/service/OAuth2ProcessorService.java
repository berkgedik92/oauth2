package project.diagram.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import project.diagram.security.model.User;
import project.diagram.security.model.UserPrincipal;
import project.diagram.security.model.AuthProviderType;
import project.diagram.security.exception.AuthProcessException;
import project.diagram.security.user.UserInfo;
import project.diagram.security.user.UserInfoFactory;
import java.util.Optional;

@Service
public class OAuth2ProcessorService extends DefaultOAuth2UserService {

    @Autowired
    private AbstractOAuthUserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();

        UserInfo userInfo = UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());
        if (StringUtils.isEmpty(userInfo.getEmail()))
            throw new AuthProcessException("Email not found from OAuth2 provider");

        Optional<User> userOptional = userService.findByEmail(userInfo.getEmail());
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (!user.getProvider().equals(AuthProviderType.valueOf(registrationId))) {
                throw new AuthProcessException("Looks like you're signed up with " +
                        user.getProvider() + " account. Please use your " + user.getProvider() + " account to login.");
            }
            user = updateExistingUser(user, userInfo);
        }
        else {
            user = registerNewUser(oAuth2UserRequest, userInfo);
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, UserInfo userInfo) {

        User user = new User();

        user.setProvider(AuthProviderType.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
        user.setProviderId(userInfo.getId());
        user.setName(userInfo.getName());
        user.setEmail(userInfo.getEmail());
        user.setImageUrl(userInfo.getImageUrl());

        userService.oauthSignupCallback(userInfo);
        userService.save(user);
        return user;
    }

    private User updateExistingUser(User existingUser, UserInfo userInfo) {
        existingUser.setName(userInfo.getName());
        existingUser.setImageUrl(userInfo.getImageUrl());

        userService.oauthSignupCallback(userInfo);
        userService.save(existingUser);
        return existingUser;
    }

}
