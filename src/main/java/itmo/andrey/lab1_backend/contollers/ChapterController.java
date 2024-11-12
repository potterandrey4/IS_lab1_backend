package itmo.andrey.lab1_backend.contollers;

import itmo.andrey.lab1_backend.entities.Chapter;
import itmo.andrey.lab1_backend.forms.SpaceMarineForm;
import itmo.andrey.lab1_backend.repositories.ChapterRepository;
import itmo.andrey.lab1_backend.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
@RequestMapping("/chapter")
public class ChapterController {
    private final ChapterRepository chapterRepository;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public ChapterController(JwtTokenUtil jwtTokenUtil, ChapterRepository chapterRepository) {
        this.chapterRepository = chapterRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody SpaceMarineForm formData, @RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(400).body("{\"error\":\"Некорректный заголовок авторизации\"}");
        }

        String tokenWithoutBearer = token.substring(7);
        boolean validToken;
        String email;
        try {
            validToken = jwtTokenUtil.validateJwtToken(tokenWithoutBearer);
            email = jwtTokenUtil.getEmailFromJwtToken(tokenWithoutBearer);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("{\"error\":\"Ошибка обработки токена: " + e.getMessage() + "\"}");
        }

        if (validToken) {
            Chapter chapter = new Chapter();
            try {
                chapter.setUserEmail(email);
                chapterRepository.save(chapter);
                return ResponseEntity.ok("{\"msg\":\"Chapter успешно добавлен}");
            } catch (Exception e) {
                return ResponseEntity.status(400).body("{\"error\":\"Произошла ошибка добавления Chapter: " + e.getMessage() + "\"}");
            }
        } else {
            return ResponseEntity.status(400).body("{\"error\":\"Ошибка обработки токена}");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(400).body("{\"error\":\"Некорректный заголовок авторизации\"}");
        }
        String tokenWithoutBearer = token.substring(7);
        boolean validToken;
        String email;
        try {
            validToken = jwtTokenUtil.validateJwtToken(tokenWithoutBearer);
            email = jwtTokenUtil.getEmailFromJwtToken(tokenWithoutBearer);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("{\"error\":\"Ошибка обработки токена: " + e.getMessage() + "\"}");
        }

        if (validToken) {
            return chapterRepository.findById(id).map(chapter -> {
                if (!chapter.getUserEmail().equals(email)) {
                    return ResponseEntity.status(403).body("{\"error\":\"Нет прав на удаление этого объекта\"}");
                }

                chapterRepository.delete(chapter);
                return ResponseEntity.ok().body("{\"message\":\"Chapter успешно удален\"}");
            }).orElseGet(() -> ResponseEntity.status(404).body("{\"error\":\"SpaceMarine не найден\"}"));
        } else {
            return ResponseEntity.status(401).body("{\"error\":\"Неверный или просроченный токен\"}");
        }
    }

}
