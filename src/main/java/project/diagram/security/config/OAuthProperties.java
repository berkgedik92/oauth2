package project.diagram.security.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import javax.validation.constraints.NotBlank;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix="oauth")
public class OAuthProperties {

    @NotBlank
    private String tokenSecret;

    @NotBlank
    private Long tokenExpirationTime;

    @NotBlank
    private Map<String, String> redirectUris;

    @NotBlank
    private String errorRedirectUri;
}