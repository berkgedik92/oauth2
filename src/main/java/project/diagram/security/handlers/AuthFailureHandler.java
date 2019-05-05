package project.diagram.security.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import project.diagram.security.service.CookieService;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static project.diagram.security.service.CookieService.REDIRECT_URI_PARAM_COOKIE_NAME;

@Component
public class AuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private CookieService cookieService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        // TODO: For redirectUri, load a Map from application.yml to detect the target url
        String redirectUri = cookieService.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse("default");

        String targetUrl = "http://localhost:7000/static/login.html";

        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("error", exception.getLocalizedMessage())
                .queryParam("redirect", redirectUri)
                .build().toUriString();

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        cookieService.removeAuthorizationRequestCookies(request, response);

        response.sendRedirect(UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("error", exception.getLocalizedMessage())
                .build().toUriString());
    }
}
