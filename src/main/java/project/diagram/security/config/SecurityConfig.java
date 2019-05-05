package project.diagram.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import project.diagram.security.handlers.UnauthenticatedUserHandler;
import project.diagram.security.filter.TokenAuthenticationFilter;
import project.diagram.security.service.OAuth2ProcessorService;
import project.diagram.security.service.CookieService;
import project.diagram.security.handlers.AuthFailureHandler;
import project.diagram.security.handlers.AuthSuccessHandler;
import project.diagram.security.service.AbstractOAuthUserService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        //To enable @Secured(...) annotation
        securedEnabled = true,
        //To enable @RolesAllowed(...) annotation
        jsr250Enabled = true,
        //To enable @PreAuthorize(...) and @PostAuthorize(...) annotations
        prePostEnabled = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /*
        Spring security needs to load users details somehow. For this reason, we create a custom service that
        implements UserDetailsService (this is a Spring specific interface). Then we give this service in "configure"
        function. This is how Spring knows how to load a user from repository when it is necessary.

        This implements only one function (which finds a user by name) and returns UserDetails object (which is an interface)
        This interface needs the implementation of functions such as getPassword(), isAccountNonExpired(), ...
    */

    @Autowired
    private AbstractOAuthUserService userService;

    @Autowired
    private OAuth2ProcessorService OAuth2ProcessorService;

    @Autowired
    private AuthSuccessHandler authSuccessHandler;

    @Autowired
    private AuthFailureHandler authFailureHandler;

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }

    /*
      By default, Spring OAuth2 uses HttpSessionOAuth2AuthorizationRequestRepository to save
      the authorization request. But, since our service is stateless, we can't save it in
      the session. We'll save the request in a Base64 encoded cookie instead.
    */
    @Bean
    public CookieService cookieAuthorizationRequestRepository() {
        return new CookieService();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

      @Override
      protected void configure(HttpSecurity http) throws Exception {
          http
                  .cors().and()
                  .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                  .csrf().disable()

                  // Disable simple and standard authentication methods such as formLogin and httpBasic.
                  // We want to only use OAuth2
                  .formLogin().disable()
                  .httpBasic().disable()

                  // If an unauthorized user will try to access to a secured resource, UnauthenticatedUserHandler
                  // class will be called and it will return the necessary response to the intruder.
                  .exceptionHandling().authenticationEntryPoint(new UnauthenticatedUserHandler()).and()
                  .authorizeRequests()

                  // We state the sources that do not require authentication, they will be allowed to any user
                  // and the rest will be protected. (TODO: Get this list from application.yml)
                  .antMatchers("/",
                          "/resources/**",
                          "/view/**",
                          "/error",
                          "/favicon.ico",
                          "*.js",
                          "*.png",
                          "/**/*.png",
                          "/**/*.gif",
                          "/**/*.svg",
                          "/**/*.jpg",
                          "/**/*.html",
                          "/**/*.css",
                          "/**/*.js",
                          "/auth/**",
                          "/oauth2/**").permitAll()

                  .anyRequest().authenticated().and()

                  /*
                      When a user will send a request to /oauth2/authorize, this request will be automatically
                      catched by OAuth2 library and handled by it
                      (actually the full URL is /oauth2/authorize/{provider}?redirect_uri=<redirect_uri_after_login>).
                      Here, we just give the prefix (any URL that contains this prefix will be handled by OAuth2)
                      The request will be handled by cookieAuthorizationRequestRepository class
                   */
                  .oauth2Login().authorizationEndpoint()
                        .baseUri("/oauth2/authorize")
                        .authorizationRequestRepository(cookieAuthorizationRequestRepository())
                        .and()
                      /*
                          On receiving the authorization request, Spring security’s OAuth2 client will redirect the
                          user to the AuthorizationUrl of the supplied provider.
                          All the state related to the authorization request is saved using the
                          authorizationRequestRepository specified in the SecurityConfig.
                          The user now allows/denies permission to your app on the provider’s page.
                          If the user allows permission to the app, the provider will redirect the user to the
                          callback url ... /oauth2/callback/{provider} with an authorization code.
                          If the user denies the permission, he will be redirected to the same callbackUrl but with an
                          error. (this URL comes from application.yml)
                       */
                      .redirectionEndpoint().baseUri("/oauth2/callback/*").and()
                      // We specify how to get user-related data and what to do in case of a failure or success...
                      .userInfoEndpoint().userService(OAuth2ProcessorService).and()
                      .successHandler(authSuccessHandler)
                      .failureHandler(authFailureHandler);

          // Add our custom Token based authentication filter
          http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
      }
}
