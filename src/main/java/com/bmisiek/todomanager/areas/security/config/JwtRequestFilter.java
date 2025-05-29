package com.bmisiek.todomanager.areas.security.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Optional;


public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService customUserDetailsService;

    public JwtRequestFilter(JwtUtil jwtUtil, UserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        tryAuthenticateJwt(request);
        chain.doFilter(request, response);
    }

    private void tryAuthenticateJwt(HttpServletRequest request) {
        if(isAuthenticated()){
            return;
        }

        var foundJwt = GetJwtToken(request);
        if(foundJwt.isEmpty()){
            return;
        }

        UserDetails userDetails = getUserDetailsByJwt(foundJwt.get());
        if (userDetails == null) {
            return;
        }

        Authenticate(request, userDetails);
    }

    private UserDetails getUserDetailsByJwt(String jwt) {
        try {
            return Optional.ofNullable(jwtUtil.extractUsername(jwt))
                    .filter(username -> !username.isEmpty())
                    .map(customUserDetailsService::loadUserByUsername)
                    .filter(userDetails -> jwtUtil.validateToken(jwt, userDetails))
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    private static void Authenticate(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        usernamePasswordAuthenticationToken
                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

    private static boolean isAuthenticated() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
               authentication.isAuthenticated() &&
               !authentication.getPrincipal().equals("anonymousUser");
    }

    private static Optional<String> GetJwtToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        var jwtPrefix = "Bearer ";
        if (authorizationHeader != null && authorizationHeader.startsWith(jwtPrefix)) {
            return Optional.of(authorizationHeader.substring(jwtPrefix.length()));
        }

        return Optional.empty();
    }
}