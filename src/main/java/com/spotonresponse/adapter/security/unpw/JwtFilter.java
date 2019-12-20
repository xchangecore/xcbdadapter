package com.spotonresponse.adapter.security.unpw;

import lombok.val;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private ConfigUserDetailsService configUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws ServletException, IOException {
        // get the authorization header.
        var authHeader = req.getHeader("Authorization");
        authHeader = authHeader == null ? req.getHeader("authorization") : authHeader;
        authHeader = authHeader == null ? "" : authHeader;


        if(!authHeader.startsWith("Bearer")){
            chain.doFilter(req, resp);
            return;
        }

        val token = authHeader.replace("Bearer", "").trim();

        String username = null;
        try {
            val tempClaims = jwtService.getClaimsFromToken(token);

            if(!jwtService.isValidToken(tempClaims)){
                throw new RuntimeException("Invalid token");
            }

            username = tempClaims.getSubject();
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }

        if(username == null){
            chain.doFilter(req, resp);
            return;
        }

        val authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null){
            ConfigUserDetails userDetails = (ConfigUserDetails) configUserDetailsService.loadUserByUsername(username);

            val usernamePasswordAuthToken = new UsernamePasswordAuthenticationToken(username, null,
                    userDetails.getAuthorities());

            usernamePasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));

            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthToken);
        }

        chain.doFilter(req, resp);
    }
}
