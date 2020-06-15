package com.reddit.redditclone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.security.SecureRandom;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class   RegisterRequest {

    private String email;
    private String username;
    private String password;
}
