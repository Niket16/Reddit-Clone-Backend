package com.reddit.redditclone.controller;

import com.reddit.redditclone.dto.RegisterRequest;
import com.reddit.redditclone.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus  ;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    @PostMapping("/signup")
    public ResponseEntity<String>signup(@RequestBody RegisterRequest registerRequest){
        authService.signup(registerRequest);
        return new ResponseEntity<>("User Registration successful",
                 OK);
    }

    @GetMapping("accountVerification/{token}")
    public ResponseEntity<String> varifyAccount(@PathVariable String token){
        authService.verifyAccount(token);
        return  new ResponseEntity<>("Account Activated Successfully", OK);
    }
}
