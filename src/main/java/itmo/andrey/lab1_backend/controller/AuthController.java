package itmo.andrey.lab1_backend.controller;

import itmo.andrey.lab1_backend.domain.SignUpRequest;
import itmo.andrey.lab1_backend.domain.dto.AdminRequestDTO;
import itmo.andrey.lab1_backend.domain.dto.SignInDTO;
import itmo.andrey.lab1_backend.domain.dto.SignUpDTO;
import itmo.andrey.lab1_backend.domain.entitie.AdminRequest;
import itmo.andrey.lab1_backend.domain.entitie.User;
import itmo.andrey.lab1_backend.repository.AdminRequestRepository;
import itmo.andrey.lab1_backend.repository.UserRepository;
import itmo.andrey.lab1_backend.service.UserCacheService;
import itmo.andrey.lab1_backend.service.UserService;
import itmo.andrey.lab1_backend.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
@RequestMapping("/auth")
public class AuthController {
    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;
    private final AdminRequestRepository adminRequestRepository;
    private final UserCacheService userCacheService;
    private final UserService userService;

    @Autowired
    public AuthController(JwtTokenUtil jwtTokenUtil, UserRepository userRepository, AdminRequestRepository adminRequestRepository, UserService userService, UserCacheService userCacheService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepository = userRepository;
        this.userCacheService = userCacheService;
        this.adminRequestRepository = adminRequestRepository;
        this.userService = userService;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signinForm(@RequestBody SignInDTO formData) {
        User user = userCacheService.getUserFromCache(formData.getName());

        if (user == null) {
            user = userRepository.findByName(formData.getName());
            if (user != null) {
                userCacheService.cacheUser(user);
            }
        }

        if (user != null && user.getPassword().equals(formData.getPassword())) {
            String jwtToken = jwtTokenUtil.generateJwtToken(user.getName());
            return ResponseEntity.ok("{\"token\":\"" + jwtToken + "\"}");
        } else {
            return ResponseEntity.status(401).body("{\"error\":\"Invalid credentials\"}");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signupForm(@RequestBody SignUpRequest signUpRequest) {
        SignUpDTO formData = signUpRequest.getFormData();
        AdminRequestDTO adminRequestDTO = signUpRequest.getAdminRequestData();

        if (formData.getName() == null || formData.getName().isEmpty()) {
            return ResponseEntity.status(400).body("{\"error\":\"Имя пользователя не может быть пустым.\"}");
        }

        List<User> allUsers = userRepository.findAll();
        boolean isFirstAdmin = allUsers.stream().noneMatch(User::isAdmin);

        User newUser = new User(formData.getName(), formData.getPassword());

        if (userRepository.existsByName(newUser.getName())) {
            return ResponseEntity.status(409).body("{\"error\":\"Логин занят. Попробуйте другой.\"}");
        }

        if (isFirstAdmin && formData.isCandidateAdmin()) {
            newUser.setAdmin(true);
        } else if (formData.isCandidateAdmin()) {
            newUser.setAdmin(false);
            AdminRequest newAdminRequest = new AdminRequest();
            newAdminRequest.setUser(newUser);
            newAdminRequest.setReason(adminRequestDTO.getReason());
            adminRequestRepository.save(newAdminRequest);
        }
        userRepository.save(newUser);
        userCacheService.cacheUser(newUser);

        String jwtToken = jwtTokenUtil.generateJwtToken(newUser.getName());
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
            String nameFromJwtToken = jwtTokenUtil.getNameFromJwtToken(tokenWithoutBearer);
            correctName = userRepository.findByName(nameFromJwtToken) != null;
        } catch (Exception e) {
            return ResponseEntity.status(400).body("{\"error\":\"Ошибка обработки токена: " + e.getMessage() + "\"}");
        }

        if (validToken && correctName) {
            return ResponseEntity.ok("{\"message\":\"Токен валиден\"}");
        }
        return ResponseEntity.status(401).body("{\"error\":\"Неверный или просроченный токен\"}");
    }

    @PostMapping("/verify-admin")
    public ResponseEntity<?> checkAdmin(@RequestHeader("Authorization") String token) {
        boolean validToken;
        boolean correctName;
        boolean isAdmin;

        try {
            validToken = jwtTokenUtil.validateJwtToken(token);
            String nameFromJwtToken = userService.extractUsername(token);
            correctName = userCacheService.isUserInCache(nameFromJwtToken) || userService.extractUsername(token) != null;
            isAdmin = userCacheService.getUserFromCache(nameFromJwtToken).isAdmin();
        } catch (Exception e) {
            return ResponseEntity.status(400).body("{\"error\":\"Ошибка обработки токена: " + e.getMessage() + "\"}");
        }

        if (validToken && correctName && isAdmin) {
            return ResponseEntity.ok("{\"message\":\"Токен валиден\"}");
        }
        return ResponseEntity.status(401).body("{\"error\":\"Неверный или просроченный токен\"}");
    }

    @PostMapping("/approve-admin")
    public ResponseEntity<?> approveAdmin(@RequestParam String username, @RequestHeader("Authorization") String token) {
        boolean validToken = userService.checkValidToken(token);
        if (!validToken) {
            return ResponseEntity.status(401).body("{\"error\":\"Неверный или просроченный токен\"}");
        }

        String adminName = userService.extractUsername(token);
        User admin = userRepository.findByName(adminName);
        if (admin == null) {
            admin = userRepository.findByName(adminName);
        }

        if (admin == null || !admin.isAdmin()) {
            return ResponseEntity.status(403).body("{\"error\":\"Доступ запрещен.\"}");
        }

        User user = userRepository.findByName(username);
        if (user == null) {
            return ResponseEntity.status(404).body("{\"error\":\"Пользователь не найден.\"}");
        }

        user.setAdmin(true);
        userRepository.save(user);

        return ResponseEntity.ok("{\"message\":\"Пользователь назначен администратором.\"}");
    }


    @GetMapping("/admin-requests")
    public ResponseEntity<?> getAdminRequests(@RequestHeader("Authorization") String token) {
        boolean validToken = userService.checkValidToken(token);
        if (!validToken) {
            return ResponseEntity.status(401).body("{\"error\":\"Неверный или просроченный токен\"}");
        }

        String adminName = userService.extractUsername(token);
        User admin = userRepository.findByName(adminName);
        if (admin == null) {
            admin = userRepository.findByName(adminName);
        }

        if (admin == null || !admin.isAdmin()) {
            return ResponseEntity.status(403).body("{\"error\":\"Доступ запрещен.\"}");
        }

        List<AdminRequest> adminRequests = adminRequestRepository.findAll();
        return ResponseEntity.ok(adminRequests);
    }

    @PostMapping("/user-status")
    public ResponseEntity<?> getUserStatus(@RequestHeader("Authorization") String token) {
        boolean validToken = jwtTokenUtil.validateJwtToken(token);
        if (!validToken) {
            return ResponseEntity.status(401).body("{\"error\":\"Неверный или просроченный токен\"}");
        }

        String adminName = userService.extractUsername(token);
        User admin = userRepository.findByName(adminName);
        if (admin == null) {
            admin = userRepository.findByName(adminName);
        }

        if (admin == null || !admin.isAdmin()) {
            return ResponseEntity.status(409).body("{\"isAdmin\":\"false\"}");
        }

        return ResponseEntity.ok("{\"isAdmin\":\"true\"}");
    }
}
