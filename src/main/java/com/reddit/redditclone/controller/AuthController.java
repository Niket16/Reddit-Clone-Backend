package com.reddit.redditclone.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.reddit.redditclone.dto.*;
import com.reddit.redditclone.exception.SpringCustomeException;
import com.reddit.redditclone.model.User;
import com.reddit.redditclone.repository.UserRepository;
import com.reddit.redditclone.security.JwtProvider;
import com.reddit.redditclone.service.AuthService;
import com.reddit.redditclone.service.RefreshTokenService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.json.simple.parser.ParseException;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder ;
    private  final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private Environment env;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody RegisterRequest registerRequest) {

        return new ResponseEntity<>(authService.signup(registerRequest),
                OK);
    }

    @PostMapping("/getGoogleJwt")
    public Object getDetailsFromJwt(@RequestBody GoogleDto googleDto) throws ParseException, IOException {
        String googleClientId = env.getProperty("google.clientId");
        final NetHttpTransport transport = new NetHttpTransport();
        final JacksonFactory jacksonFactory = new JacksonFactory();
        GoogleIdTokenVerifier.Builder verifier = new GoogleIdTokenVerifier.Builder(transport,jacksonFactory)
                .setAudience(Collections.singletonList(googleClientId));

        final GoogleIdToken googleIdToken = GoogleIdToken.parse(verifier.getJsonFactory(),googleDto.getToken());

        final GoogleIdToken.Payload payload = googleIdToken.getPayload();
        User user = userRepository.findTop1ByEmail(payload.getEmail());
        if(user != null) {
            if(!user.getProviderName().equalsIgnoreCase("google")){
                return "You were signed up as a "+user.getProviderName()+" user please signup using "+user.getProviderName()+" service";
            }
            else{
                return login(user,"google");
            }
        }
        else {
            user = saveUser(payload.getEmail(),"google");

            return login(user,"google");
        }






//		RestTemplate restTemplate = new RestTemplate();
//		final String uri = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token="+googleDto.getToken();
//		log.info(uri);
//		JSONObject result =
//				restTemplate.getForObject(uri, JSONObject.class);
//		org.json.simple.parser.JSONParser parser = new JSONParser();
//		org.json.simple.JSONObject json = (org.json.simple.JSONObject) parser.parse(result);
//		return result.get("email").toString();


    }

    @PostMapping("/amazon")
    public Object getAmazonDetails(@RequestBody GoogleDto googleDto) throws ParseException, IOException {
        log.info(googleDto.getToken());


        Content c = Request.Get("https://api.amazon.com/user/profile")
                .addHeader("Authorization", "bearer " + googleDto.getToken())
                .execute()
                .returnContent();

        Map m = new ObjectMapper().readValue(c.toString(), new TypeReference<Map>() {
        });

        User user = userRepository.findTop1ByEmail(m.get("email").toString());
        log.info(m.toString());
        if (user != null) {
            if (!user.getProviderName().equalsIgnoreCase("amazon")) {
                return "You were signed up as a " + user.getProviderName() + " user please signup using " + user.getProviderName() + " service";
            } else {
                return login(user,"amazon");
            }
        } else {
            user = saveUser(m.get("email").toString(),"amazon");

            return login(user,"amazon");
        }
    }



    private AuthenticationResponse login(User user,String providerName) {

        String defaultPass="";
        if(providerName.equalsIgnoreCase("google")) {

            defaultPass = env.getProperty("pass");
        }
        else if(providerName.equalsIgnoreCase("amazon"))
        {

            defaultPass = env.getProperty("amazonPass");
        }
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUserName(),defaultPass));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateToken(authentication);
        GoogleDto googleDto = new GoogleDto();
        googleDto.setToken(jwt);
        return  AuthenticationResponse.builder()
                .authenticationToken(jwt)
                .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                .expireAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(user.getUserName())
                .build();
    }
    private  User saveUser(String email,String providerName) {

        String defaultPass="";
        if(providerName.equalsIgnoreCase("google")) {

            defaultPass = env.getProperty("pass");
        }
        else if(providerName.equalsIgnoreCase("amazon"))
        {

            defaultPass = env.getProperty("amazonPass");
        }
        User user = new User();
        user.setUserName(email.substring(0,email.indexOf("@")));
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(defaultPass));
        user.setCreated(Instant.now());
        user.setEnabled(true);
        user.setProviderName(providerName);
        userRepository.save(user);


        return  user;

    }

    @GetMapping("accountVerification/{token}")
    public ResponseEntity<String> varifyAccount(@PathVariable String token) {
        authService.verifyAccount(token);
        return new ResponseEntity<>("Account Activated Successfully", OK);
    }

    @PostMapping("/login")
    public Object login(@RequestBody LoginRequest loginRequest) {

        User user = userRepository.findByUserName(loginRequest.getUsername()).orElseThrow(()->new SpringCustomeException("No such User Found"));
        if(user!=null) {
            if (user.getProviderName().equalsIgnoreCase("local")) {
                return authService.login(loginRequest);
            } else {
                return "You were signed up as a "+user.getProviderName()+" user. please login using "+user.getProviderName()+" service";
            }
        }
        else{
            return "No such user exists";
        }
    }

    @PostMapping("/refresh/token")
    public AuthenticationResponse refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return authService.refreshToken(refreshTokenRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.status(OK).body("Refresh Token deleted successfully");
    }
}


