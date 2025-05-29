package com.bmisiek.todomanager.unit.areas.security.service;

import com.bmisiek.todomanager.areas.security.config.JwtUtil;
import com.bmisiek.todomanager.areas.security.dto.LoginDto;
import com.bmisiek.todomanager.areas.security.entity.User;
import com.bmisiek.todomanager.areas.security.repository.UserRepository;
import com.bmisiek.todomanager.areas.security.service.UserJwtAuthenticator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserJwtAuthenticatorTest {

    @Mock
    private AuthenticationManager authenticationManager;
    
    @Mock
    private UserDetailsService userDetailsService;
    
    @Mock
    private JwtUtil jwtUtil;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private Authentication authentication;
    
    @Mock
    private SecurityContext securityContext;
    
    @Mock
    private UserDetails userDetails;

    private UserJwtAuthenticator authenticator;

    @BeforeEach
    public void setUp() {
        authenticator = new UserJwtAuthenticator(authenticationManager, userDetailsService, jwtUtil, userRepository);
    }

    @Test
    public void Should_Authenticate_AndReturnToken() {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsernameOrEmail("testuser");
        loginDto.setPassword("password");
        
        String token = "jwtToken";

        try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            Mockito.when(securityContext.getAuthentication()).thenReturn(null);

            Mockito.when(userDetailsService.loadUserByUsername(loginDto.getUsernameOrEmail())).thenReturn(userDetails);
            Mockito.when(jwtUtil.generateToken(userDetails)).thenReturn(token);

            String result = authenticator.authenticate(loginDto);

            Assertions.assertEquals(token, result);
            Mockito.verify(authenticationManager).authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsernameOrEmail(), loginDto.getPassword()));
        }
    }

    @Test
    public void Should_ThrowException_WhenAlreadyAuthenticated() {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsernameOrEmail("testuser");
        loginDto.setPassword("password");
        
        try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
            Mockito.when(authentication.isAuthenticated()).thenReturn(true);
            Mockito.when(authentication.getPrincipal()).thenReturn("user");
            
            IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class,
                    () -> authenticator.authenticate(loginDto));
            
            Assertions.assertEquals("User is already authenticated.", exception.getMessage());
            Mockito.verify(authenticationManager, Mockito.never()).authenticate(ArgumentMatchers.any());
        }
    }

    @Test
    public void Should_Logout() {
        try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
            Mockito.when(authentication.isAuthenticated()).thenReturn(true);
            Mockito.when(authentication.getPrincipal()).thenReturn("user");
            
            authenticator.logout();
            
            Mockito.verify(securityContext).getAuthentication();
            mockedStatic.verify(SecurityContextHolder::clearContext);
        }
    }

    @Test
    public void Should_ThrowException_WhenLogoutNotAuthenticated() {
        try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            Mockito.when(securityContext.getAuthentication()).thenReturn(null);
            
            IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class,
                    () -> authenticator.logout());
            
            Assertions.assertEquals("User is not authenticated.", exception.getMessage());
            mockedStatic.verify(SecurityContextHolder::clearContext, Mockito.never());
        }
    }

    @Test
    public void Should_GetAuthenticatedUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        
        try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
            Mockito.when(authentication.isAuthenticated()).thenReturn(true);
            Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
            Mockito.when(userDetails.getUsername()).thenReturn("testuser");
            Mockito.when(userRepository.findByUsernameOrEmail(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Optional.of(user));
            
            User result = authenticator.getAuthenticatedUser();
            
            Assertions.assertNotNull(result);
            Assertions.assertEquals(1L, result.getId());
            Assertions.assertEquals("testuser", result.getUsername());
        }
    }

    @Test
    public void Should_ThrowException_WhenGetAuthenticatedUserNotAuthenticated() {
        try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            Mockito.when(securityContext.getAuthentication()).thenReturn(null);
            
            IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class,
                    () -> authenticator.getAuthenticatedUser());
            
            Assertions.assertEquals("User is not authenticated.", exception.getMessage());
        }
    }

    @Test
    public void Should_ThrowException_WhenAuthenticatedUserNotFound() {
        try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
            Mockito.when(authentication.isAuthenticated()).thenReturn(true);
            Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
            Mockito.when(userDetails.getUsername()).thenReturn("testuser");
            Mockito.when(userRepository.findByUsernameOrEmail(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Optional.empty());
            
            IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class,
                    () -> authenticator.getAuthenticatedUser());
            
            Assertions.assertEquals("Authenticated user not found in the database.", exception.getMessage());
        }
    }
}
