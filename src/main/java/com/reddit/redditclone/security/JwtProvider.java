package com.reddit.redditclone.security;


//import com.reddit.redditclone.model.User;
import com.reddit.redditclone.exception.SpringCustomeException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

@Service
public class JwtProvider {
    private KeyStore keyStore;

    @PostConstruct
    public void init(){
        try{
            keyStore = KeyStore.getInstance("JKS");
            InputStream  resourceAsStream = getClass().getResourceAsStream("/redditclone.jks");

            keyStore.load(resourceAsStream, "niketpatel".toCharArray());

        }catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e){
            throw new SpringCustomeException("Exception occured while keystore");
        }
    }


    public String generateToken(Authentication authentication){
        User principal = (User) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .signWith(getPrivateKey())
                .compact();
    }

    private PrivateKey getPrivateKey() {
        try{
            return (PrivateKey) keyStore.getKey("redditclone", "niketpatel".toCharArray());
        }catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e){
            throw  new SpringCustomeException("Exception occured while retriving private key from keystrom");
        }
    }

    public boolean validateToken(String jwt){
        Jwts.parser().setSigningKey(getPublickey()).parseClaimsJws(jwt);
        return true;
    }

    private PublicKey getPublickey() {
        try{
            return keyStore.getCertificate("redditclone").getPublicKey();
        }catch (KeyStoreException e){
            throw  new SpringCustomeException("Exception occured while retriving public key from keystrom");
        }
    }

    public String getUsernameFromToken(String token){
        Claims claims = Jwts.parser().setSigningKey(getPublickey()).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

}
