package com.dev.java_flix.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{

    private JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String jwt = extractJwtToken(request);
        String username = jwtUtil.getUsernameFromToken(jwt);

        if (shouldProcessAuthentication(username)){
            processAuthentication(request, jwt, username);
        }
    }

    // Extrai o token do Header 'Authorization' ou de um parâmetro na URL
    private String extractJwtToken(HttpServletRequest request){
        final String authorizationHeader = request.getHeader("Authorization");
        final String requestURI = request.getRequestURI();

        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            return authorizationHeader.substring(7);
        } 
        // Permite token via parâmetro de URL apenas para endpoints de arquivos/streaming
        else if ((requestURI.contains("/api/files/video/") || requestURI.contains("/api/files/image") && request.getParameter("token") != null)){
            return request.getParameter("token");
        }

        return null;
    }

    private boolean shouldProcessAuthentication(String username){
        return username != null && SecurityContextHolder.getContext().getAuthentication() == null;
    }

    // Valida o token e inicia o processo de criação de sessão do usuário no contexto de segurança
    private void processAuthentication(HttpServletRequest request, String jwt, String username) {
            
        if(jwtUtil.validateToken(jwt)){
            UserDetails userDetails = createUserDetailsFromToken(jwt, username);
            setAuthenticationInContext(request, userDetails);
        }
    }

    // Cria um objeto UserDetails a partir dos dados (claims) contidos no JWT
    private UserDetails createUserDetailsFromToken(String jwt, String username){
        
        String role = jwtUtil.getRoleFromToken(jwt);

        return User.builder()
                .username(username)
                .password("")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_"+role)))
                .build();
    }

    // Registra usuário autenticado no SecurityContext
    private void setAuthenticationInContext(HttpServletRequest request, UserDetails userDetails) {
        
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

}
