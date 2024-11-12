package itmo.andrey.lab1_backend.utils;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenUtil {

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${jwt.expiration}")
	private Long jwtExpiration;

	public String generateJwtToken(String email) {
		System.out.println("Generating token for email: " + email); // Логирование email при генерации
		return Jwts.builder()
				.setSubject(email)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	}


	public boolean validateJwtToken(String token) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
			return true;
		} catch (MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public String getEmailFromJwtToken(String token) {
		try {
			String email = Jwts.parser()
					.setSigningKey(jwtSecret)
					.parseClaimsJws(token)
					.getBody()
					.getSubject();
			return email;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
