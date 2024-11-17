package itmo.andrey.lab1_backend.controller;

import itmo.andrey.lab1_backend.domain.entitie.Chapter;
import itmo.andrey.lab1_backend.domain.entitie.SpaceMarine;
import itmo.andrey.lab1_backend.domain.dto.SpaceMarineDTO;
import itmo.andrey.lab1_backend.repository.ChapterRepository;
import itmo.andrey.lab1_backend.repository.SpaceMarineRepository;
import itmo.andrey.lab1_backend.util.JwtTokenUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
@RequestMapping("/space-marine")
public class SpaceMarineController {
	private final SpaceMarineRepository spaceMarineRepository;
	private final JwtTokenUtil jwtTokenUtil;
	private final ChapterRepository chapterRepository;

	@Autowired
	public SpaceMarineController(JwtTokenUtil jwtTokenUtil, SpaceMarineRepository spaceMarineRepository, ChapterRepository chapterRepository) {
		this.spaceMarineRepository = spaceMarineRepository;
		this.jwtTokenUtil = jwtTokenUtil;
		this.chapterRepository = chapterRepository;
	}

	@PostMapping("/add")
	public ResponseEntity<?> add(@Valid @RequestBody SpaceMarineDTO formData, @RequestHeader("Authorization") String token) {
		if (token == null || !token.startsWith("Bearer ")) {
			return ResponseEntity.status(400).body("{\"error\":\"Некорректный заголовок авторизации\"}");
		}

		String tokenWithoutBearer = token.substring(7);
		boolean validToken;
		String userName;
		try {
			validToken = jwtTokenUtil.validateJwtToken(tokenWithoutBearer);
			userName = jwtTokenUtil.getNameFromJwtToken(tokenWithoutBearer);
		} catch (Exception e) {
			return ResponseEntity.status(400).body("{\"error\":\"Ошибка обработки токена: " + e.getMessage() + "\"}");
		}

		if (validToken) {
			SpaceMarine spaceMarine = new SpaceMarine();
			try {
				if (formData.getName() == null || formData.getCategory() == null) {
					return ResponseEntity.status(400).body("{\"error\":\"Required fields are missing\"}");
				}

				spaceMarine.setName(formData.getName());
				spaceMarine.setCoordinates_x(formData.getCoordinates().getX());
				spaceMarine.setCoordinates_y(formData.getCoordinates().getY());
				spaceMarine.setCreationDate(String.valueOf(java.time.LocalDateTime.now()));
				spaceMarine.setHealth(formData.getHealth());
				spaceMarine.setHeight(formData.getHeight());
				spaceMarine.setCategory(formData.getCategory());
				spaceMarine.setWeaponType(formData.getWeaponType());
				spaceMarine.setUserName(userName);

				String idChapter = formData.getChapter().getId();
				if (!Objects.equals(idChapter, "")) {
					spaceMarine.setChapter(chapterRepository.findById(Long.parseLong(idChapter)));
				} else {
					Chapter newChapter = new Chapter(userName, formData.getChapter().getName(), formData.getChapter().getMarinesCount(), formData.getChapter().getWorld());
					chapterRepository.save(newChapter);
					spaceMarine.setChapter(newChapter);
				}

				spaceMarineRepository.save(spaceMarine);
			} catch (Exception e) {
				return ResponseEntity.status(400).body("{\"error\":\"" + e.getMessage() + "\"}");
			}
			return ResponseEntity.ok().body("{\"message\":\"SpaceMarine успешно добавлен\"}");
		}
		else {
			return ResponseEntity.status(401).body("{\"error\":\"Неверный или просроченный токен\"}");
		}
	}

	@PostMapping("get/{id}")
	public ResponseEntity<?> get(@PathVariable("id") Long id, @RequestHeader("Authorization") String token) {
		if (token == null || !token.startsWith("Bearer ")) {
			return ResponseEntity.status(400).body("{\"error\":\"Некорректный заголовок авторизации\"}");
		}
		String tokenWithoutBearer = token.substring(7);
		boolean validToken;
		try {
			validToken = jwtTokenUtil.validateJwtToken(tokenWithoutBearer);
		} catch (Exception e) {
			return ResponseEntity.status(400).body("{\"error\":\"Ошибка обработки токена: " + e.getMessage() + "\"}");
		}

		if (validToken) {
			return ResponseEntity.ok().body(spaceMarineRepository.findById(id));
		} else {
			return ResponseEntity.status(400).body("{\"error\":\"Данного id не существует\"}");
		}
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<?> update(@PathVariable Long id, @RequestBody SpaceMarineDTO formData, @RequestHeader("Authorization") String token) {
		if (token == null || !token.startsWith("Bearer ")) {
			return ResponseEntity.status(400).body("{\"error\":\"Некорректный заголовок авторизации\"}");
		}

		String tokenWithoutBearer = token.substring(7);
		boolean validToken;
		String userName;
		try {
			validToken = jwtTokenUtil.validateJwtToken(tokenWithoutBearer);
			userName = jwtTokenUtil.getNameFromJwtToken(tokenWithoutBearer);
		} catch (Exception e) {
			return ResponseEntity.status(400).body("{\"error\":\"Ошибка обработки токена: " + e.getMessage() + "\"}");
		}

		if (validToken) {
			return spaceMarineRepository.findById(id).map(spaceMarine -> {
				if (!spaceMarine.getUserName().equals(userName)) {
					return ResponseEntity.status(403).body("{\"error\":\"Нет прав на изменение этого объекта\"}");
				}

				try {
					if (formData.getName() == null || formData.getCategory() == null) {
						return ResponseEntity.status(400).body("{\"error\":\"Required fields are missing\"}");
					}

					spaceMarine.setName(formData.getName());
					spaceMarine.setCoordinates_x(formData.getCoordinates().getX());
					spaceMarine.setCoordinates_y(formData.getCoordinates().getY());
					spaceMarine.setChapter(new Chapter(userName, formData.getChapter().getName(), formData.getChapter().getMarinesCount(), formData.getChapter().getWorld()));
					spaceMarine.setHealth(formData.getHealth());
					spaceMarine.setHeight(formData.getHeight());
					spaceMarine.setCategory(formData.getCategory());
					spaceMarine.setWeaponType(formData.getWeaponType());

					spaceMarineRepository.save(spaceMarine);
					return ResponseEntity.ok().body("{\"message\":\"SpaceMarine успешно обновлен\"}");
				} catch (Exception e) {
					return ResponseEntity.status(400).body("{\"error\":\"" + e.getMessage() + "\"}");
				}
			}).orElseGet(() -> ResponseEntity.status(404).body("{\"error\":\"SpaceMarine не найден\"}"));
		} else {
			return ResponseEntity.status(401).body("{\"error\":\"Неверный или просроченный токен\"}");
		}
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id, @RequestHeader("Authorization") String token) {
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
			return spaceMarineRepository.findById(id).map(spaceMarine -> {
				if (!spaceMarine.getUserName().equals(name)) {
					return ResponseEntity.status(403).body("{\"error\":\"Нет прав на удаление этого объекта\"}");
				}

				spaceMarineRepository.delete(spaceMarine);
				return ResponseEntity.ok().body("{\"message\":\"SpaceMarine успешно удален\"}");
			}).orElseGet(() -> ResponseEntity.status(404).body("{\"error\":\"SpaceMarine не найден\"}"));
		} else {
			return ResponseEntity.status(401).body("{\"error\":\"Неверный или просроченный токен\"}");
		}
	}

	@PostMapping("/all-objects")
	public ResponseEntity<?> getAllObjects() {
		List<SpaceMarine> allMarines = spaceMarineRepository.findAll();
		return ResponseEntity.ok(allMarines);
	}

	@PostMapping("/user-objects")
	public ResponseEntity<?> getUserObjects(@RequestHeader("Authorization") String token) {
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
			List<SpaceMarine> userMarines = spaceMarineRepository.findByUserName(name);
			return ResponseEntity.ok(userMarines);
		} else {
			return ResponseEntity.status(401).body("{\"error\":\"Неверный или просроченный токен\"}");
		}
	}
}
