package com.bmisiek.todomanager.unit.areas.security.config;

import com.bmisiek.todomanager.areas.security.config.JwtUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class JwtUtilTest {

    private JwtUtil jwtUtil;
    private UserDetails userDetails;

    @BeforeEach
    public void setUp() {
        jwtUtil = new JwtUtil(Date::new);
        userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetails.getUsername()).thenReturn("testuser");
    }

    @Test
    public void Should_GenerateToken_WithUsername() throws Exception {
        String token = jwtUtil.generateToken(userDetails);
        
        Assertions.assertNotNull(token);
        String username = jwtUtil.extractUsername(token);
        Assertions.assertEquals("testuser", username);
    }

    @Test
    public void Should_ValidateToken_WhenValid() {
        String token = jwtUtil.generateToken(userDetails);
        
        Assertions.assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    public void Should_NotValidateToken_WhenUsernameDoesNotMatch() {
        String token = jwtUtil.generateToken(userDetails);
        
        UserDetails differentUser = Mockito.mock(UserDetails.class);
        Mockito.when(differentUser.getUsername()).thenReturn("differentuser");
        
        Assertions.assertFalse(jwtUtil.validateToken(token, differentUser));
    }

    @Test
    public void Should_NotValidateToken_WhenExpired() {
        JwtUtil shortExpirationJwtUtil = new JwtUtil(Date::new) {
            @Override
            protected Date getExpirationDate() {
                return new Date(System.currentTimeMillis() - 1000);
            }
        };
        
        String expiredToken = shortExpirationJwtUtil.generateToken(userDetails);
        Assertions.assertFalse(shortExpirationJwtUtil.validateToken(expiredToken, userDetails));
    }

    @Test
    public void Should_ExtractExpiration_FromToken() throws Exception {
        String token = jwtUtil.generateToken(userDetails);
        Date expiration = jwtUtil.extractExpiration(token);
        
        Assertions.assertNotNull(expiration);
        Assertions.assertTrue(expiration.after(new Date()));
    }
}
