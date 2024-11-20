package itmo.andrey.lab1_backend.service;

import itmo.andrey.lab1_backend.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserService {
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public UserService(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public boolean checkValidToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return false;
        }

        String tokenWithoutBearer = token.substring(7);
        boolean validToken;
        try {
            validToken = jwtTokenUtil.validateJwtToken(tokenWithoutBearer);
        } catch (Exception e) {
            return false;
        }
        return validToken;
    }

    public String extractUsername(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }

        String tokenWithoutBearer = token.substring(7);
        String userName;
        try {
            userName = jwtTokenUtil.getNameFromJwtToken(tokenWithoutBearer);
        } catch (Exception e) {
            return null;
        }
        return userName;
    }
}
