package com.bmisiek.todomanager.areas.security.config;


import com.bmisiek.libraries.datetime.IDateTimeProvider;
import io.jsonwebtoken.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {
    private final String SECRET_KEY = "secret";
    private final IDateTimeProvider timeProvider;

    public JwtUtil(IDateTimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    public String extractUsername(String token) throws Exception {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) throws Exception {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws Exception {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) throws Exception {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) throws Exception {
        return extractExpiration(token).before(timeProvider.now());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(timeProvider.now())
                .setExpiration(getExpirationDate())
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    protected Date getExpirationDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timeProvider.now());
        calendar.add(Calendar.HOUR, 10);
        return calendar.getTime();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }
}
