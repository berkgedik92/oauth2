package project.diagram.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import project.diagram.security.model.User;
import project.diagram.security.service.AbstractOAuthUserService;
import project.diagram.security.model.AuthProviderType;
import project.diagram.security.payload.ApiResponse;
import project.diagram.security.payload.AuthResponse;
import project.diagram.security.payload.LoginRequest;
import project.diagram.security.payload.SignupRequest;
import project.diagram.security.service.TokenProvider;
import javax.validation.Valid;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AbstractOAuthUserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenProvider tokenProvider;

    @PostMapping("/login")
    @CrossOrigin
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.createToken(authentication);
        return new ResponseEntity<>(new AuthResponse(token), HttpStatus.OK);
    }

    @PostMapping("/signup")
    @CrossOrigin
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        if (userService.existsByEmail(signUpRequest.getEmail()))
            return new ResponseEntity<>(new ApiResponse(false, "There is already a user with this e-mail."),
                                                        HttpStatus.BAD_REQUEST);

        User user = new User();
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setProvider(AuthProviderType.local);

        userService.save(user);
        userService.regularSignupCallback(signUpRequest);

        return new ResponseEntity<>(new ApiResponse(true, "User is signed up successfully."), HttpStatus.OK);
    }
}