package com.nbs.nbsback.interceptors;

import com.nbs.nbsback.repositories.AthleteRepository;
import com.nbs.nbsback.services.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Component
public class TokenValidationInterceptor implements HandlerInterceptor {

    @Lazy
    @Autowired
    private TokenService tokenService;

    @Autowired
    private AthleteRepository athleteRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization header");
            return false;
        }


        
        // Additional token validation logic can be added here

        if(athleteRepository.findById(new Long(token.substring(7))).orElse(null) == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid token: Athlete not found");
            return false;            
        }

        return true;
    }
}