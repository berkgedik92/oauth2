package project.diagram.security.user;

import project.diagram.security.model.AuthProviderType;
import project.diagram.security.exception.AuthProcessException;
import java.util.Map;

public class UserInfoFactory {

    public static UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {

        if (registrationId.equalsIgnoreCase(AuthProviderType.google.toString()))
            return new GoogleUserInfo(attributes);

        else if (registrationId.equalsIgnoreCase(AuthProviderType.facebook.toString()))
            return new FacebookUserInfo(attributes);

        else
            throw new AuthProcessException("Sorry! Login with " + registrationId + " is not supported yet.");
    }
}
