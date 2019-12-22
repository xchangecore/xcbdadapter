package com.spotonresponse.adapter.security.unpw;

import com.spotonresponse.adapter.model.unpw.ConfigurationFileAssociation;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    String generateToken(ConfigurationFileAssociation configFileAssoc){
        val key = Keys.hmacShaKeyFor(secret.getBytes());

        return Jwts.builder().setSubject(configFileAssoc.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expirationDate())
                .signWith(key)
                .compact();
    }

    private Date expirationDate(){
        return this.expirationDate(15L);
    }

    private Date expirationDate(Long durationInMinutes){
        val currentDateTime = LocalDateTime.now();
        val expirationDate = currentDateTime.plusMinutes(durationInMinutes);
        val expirationDateInstance = expirationDate.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(expirationDateInstance);
    }

    public String generateToken(String subject, Long expirationDuration) {
        val key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.builder().setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(expirationDate(expirationDuration))
                .signWith(key)
                .compact();

    }

    public String generateToken(String subject){
        return generateToken(subject, 15L);
    }

    public boolean isValidToken(Claims claims) {
        val expirationDate = claims.getExpiration();
        return expirationDate.after(new Date());
    }


    public Claims getClaimsFromToken(String token){
        val key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
    }
}
