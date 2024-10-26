package itmo.andrey.lab1_backend.contollers;

import itmo.andrey.lab1_backend.entities.User;
import itmo.andrey.lab1_backend.models.SigninForm;
import itmo.andrey.lab1_backend.models.SignupForm;
import itmo.andrey.lab1_backend.repositories.UserRepository;
import itmo.andrey.lab1_backend.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/auth")
public class AuthController {

	private final JwtTokenUtil jwtTokenUtil;
	private final UserRepository userRepository;

	@Autowired
	public AuthController(JwtTokenUtil jwtTokenUtil, UserRepository userRepository) {
		this.jwtTokenUtil = jwtTokenUtil;
		this.userRepository = userRepository;
	}

	@PostMapping("/signin")
	public ResponseEntity<?> signinForm(@RequestBody SigninForm formData) {
		User user = userRepository.findByEmail(formData.getEmail());
		if (user != null && user.getPassword().equals(formData.getPassword())) {
			String jwtToken = jwtTokenUtil.generateJwtToken(user.getEmail());
			return ResponseEntity.ok("{\"token\":\"" + jwtToken + "\"}");
		} else {
			return ResponseEntity.status(401).body("{\"error\":\"Invalid credentials\"}");
		}
	}

	@PostMapping("/signup")
	public ResponseEntity<?> signupForm(@RequestBody SignupForm formData) {
		User newUser = new User(formData.getName(), formData.getEmail(), formData.getPassword());
		String jwtToken = jwtTokenUtil.generateJwtToken(newUser.getEmail());
		if (userRepository.existsByEmail(newUser.getEmail())) {
			return ResponseEntity.status(409).body("{\"error\":\"" + "данный email занят, попробуйте другой или войдите в существующий аккаунт" + "\"}");
		} else {
			try {
				userRepository.save(newUser);
			} catch (Exception e) {
				return ResponseEntity.status(400).body("{\"error\":\"" + e.getMessage() + "\"}");
			}
		}
		return ResponseEntity.ok("{\"token\":\"" + jwtToken + "\"}");
	}

	@PostMapping("/verify-token")
	public ResponseEntity<?> checkToken(@RequestHeader("Authorization") String token) {
		if (token == null || !token.startsWith("Bearer ")) {
			return ResponseEntity.status(400).body("{\"error\":\"Некорректный заголовок авторизации\"}");
		}

		String tokenWithoutBearer = token.substring(7);
		boolean validToken;
		boolean correctName;

		try {
			validToken = jwtTokenUtil.validateJwtToken(tokenWithoutBearer);
			String emailFromToken = jwtTokenUtil.getUsernameFromJwtToken(tokenWithoutBearer);
			correctName = userRepository.findByEmail(emailFromToken) != null;
		} catch (Exception e) {
			return ResponseEntity.status(400).body("{\"error\":\"Ошибка обработки токена: " + e.getMessage() + "\"}");
		}

		if (validToken && correctName) {
			return ResponseEntity.ok("{\"message\":\"Токен валиден\"}");
		}
		return ResponseEntity.status(401).body("{\"error\":\"Неверный или просроченный токен\"}");
	}
}
