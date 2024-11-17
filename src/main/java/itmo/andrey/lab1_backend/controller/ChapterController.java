package itmo.andrey.lab1_backend.controller;

import itmo.andrey.lab1_backend.domain.entitie.Chapter;
import itmo.andrey.lab1_backend.domain.entitie.SpaceMarine;
import itmo.andrey.lab1_backend.domain.dto.ChapterDTO;
import itmo.andrey.lab1_backend.repository.ChapterRepository;
import itmo.andrey.lab1_backend.repository.SpaceMarineRepository;
import itmo.andrey.lab1_backend.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
@RequestMapping("/chapter")
public class ChapterController {
    private final ChapterRepository chapterRepository;
    private final SpaceMarineRepository spaceMarineRepository;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public ChapterController(JwtTokenUtil jwtTokenUtil, ChapterRepository chapterRepository, SpaceMarineRepository spaceMarineRepository) {
        this.chapterRepository = chapterRepository;
        this.spaceMarineRepository = spaceMarineRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody ChapterDTO formData, @RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(400).body("{\"error\":\"Некорректный заголовок авторизации\"}");
        }

        String tokenWithoutBearer = token.substring(7);
        boolean validToken;
        String name;
        try {
            validToken = jwtTokenUtil.validateJwtToken(tokenWithoutBearer);
            name = jwtTokenUtil.getNameFromJwtToken(tokenWithoutBearer);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("{\"error\":\"Ошибка обработки токена: " + e.getMessage() + "\"}");
        }

        if (validToken) {
            Chapter chapter = new Chapter();
            try {
                chapter.setUserName(name);
                chapter.setName(formData.getName());
                chapter.setCount(formData.getMarinesCount());
                chapter.setWorld(formData.getWorld());
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
    public ResponseEntity<?> deleteChapterWithReassignment(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token,
            @RequestParam("newChapterId") Long newChapterId) {

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(400).body("{\"error\":\"Некорректный заголовок авторизации\"}");
        }
        String tokenWithoutBearer = token.substring(7);
        boolean validToken;
        String name;
        try {
            validToken = jwtTokenUtil.validateJwtToken(tokenWithoutBearer);
            name = jwtTokenUtil.getNameFromJwtToken(tokenWithoutBearer);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("{\"error\":\"Ошибка обработки токена: " + e.getMessage() + "\"}");
        }

        if (validToken) {
            return chapterRepository.findById(id).map(chapter -> {
                if (!chapter.getUserName().equals(name)) {
                    return ResponseEntity.status(403).body("{\"error\":\"Нет прав на удаление этого объекта\"}");
                }

                List<SpaceMarine> marines = spaceMarineRepository.findByChapter(chapter);
                Chapter newChapter = chapterRepository.findById(newChapterId).orElse(null);
                if (newChapter == null) {
                    return ResponseEntity.status(400).body("{\"error\":\"Новый орден не найден\"}");
                }

                for (SpaceMarine marine : marines) {
                    marine.setChapter(newChapter);
                    spaceMarineRepository.save(marine);
                }

                chapterRepository.delete(chapter);
                return ResponseEntity.ok().body("{\"message\":\"Chapter успешно удален и космодесантники переназначены\"}");
            }).orElseGet(() -> ResponseEntity.status(404).body("{\"error\":\"Chapter не найден\"}"));

        } else {
            return ResponseEntity.status(401).body("{\"error\":\"Неверный или просроченный токен\"}");
        }
    }

    @PostMapping("/getAll")
    public ResponseEntity<?> getAllChapters() {
        List<Chapter> allChapters = chapterRepository.findAll();
        return ResponseEntity.ok(allChapters);
    }

}
