package project.diagram.security.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import project.diagram.security.config.OAuthProperties;
import project.diagram.security.service.TokenProvider;
import project.diagram.security.service.CookieService;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static project.diagram.security.service.CookieService.REDIRECT_URI_PARAM_COOKIE_NAME;

@Component
public class AuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private OAuthProperties properties;

    @Autowired
    private CookieService cookieService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // TODO: For redirectUri, load a Map from application.yml to detect the target url

        String redirectParam = cookieService.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                                            .map(Cookie::getValue)
                                            .orElse("default");

        String targetUrl = properties.getRedirectUris().get(redirectParam);

        String token = tokenProvider.createToken(authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        super.clearAuthenticationAttributes(request);
        cookieService.removeAuthorizationRequestCookies(request, response);

        response.sendRedirect(UriComponentsBuilder.fromUriString(targetUrl)
                                .queryParam("token", token)
                                .build().toUriString());
    }
}
