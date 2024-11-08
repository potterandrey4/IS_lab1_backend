package itmo.andrey.lab1_backend.contollers;

import itmo.andrey.lab1_backend.entities.SpaceMarine;
import itmo.andrey.lab1_backend.forms.SpaceMarineForm;
import itmo.andrey.lab1_backend.repositories.SpaceMarineRepository;
import itmo.andrey.lab1_backend.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
@RequestMapping("/space-marine")
public class SpaceMarineController {
	private final SpaceMarineRepository spaceMarineRepository;
	private final JwtTokenUtil jwtTokenUtil;

	@Autowired
	public SpaceMarineController(JwtTokenUtil jwtTokenUtil, SpaceMarineRepository spaceMarineRepository) {
		this.spaceMarineRepository = spaceMarineRepository;
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
			SpaceMarine spaceMarine = new SpaceMarine();
			try {
				if (formData.getName() == null || formData.getCategory() == null) {
					return ResponseEntity.status(400).body("{\"error\":\"Required fields are missing\"}");
				}

				spaceMarine.setName(formData.getName());
				spaceMarine.setCoordinates_x(formData.getCoordinates().getX());
				spaceMarine.setCoordinates_y(formData.getCoordinates().getY());
				spaceMarine.setCreationDate(String.valueOf(java.time.LocalDateTime.now()));
				spaceMarine.setChapter_name(formData.getChapter().getName());
				spaceMarine.setChapter_marinesCount(formData.getChapter().getMarinesCount());
				spaceMarine.setChapter_world(formData.getChapter().getWorld());
				spaceMarine.setHealth(formData.getHealth());
				spaceMarine.setHeight(formData.getHeight());
				spaceMarine.setCategory(formData.getCategory());
				spaceMarine.setWeaponType(formData.getWeaponType());
				spaceMarine.setUser_email(email);

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

	@PutMapping("/update/{id}")
	public ResponseEntity<?> update(@PathVariable Long id, @RequestBody SpaceMarineForm formData, @RequestHeader("Authorization") String token) {
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
			return spaceMarineRepository.findById(id).map(spaceMarine -> {
				if (!spaceMarine.getUser_email().equals(email)) {
					return ResponseEntity.status(403).body("{\"error\":\"Нет прав на изменение этого объекта\"}");
				}

				try {
					if (formData.getName() == null || formData.getCategory() == null) {
						return ResponseEntity.status(400).body("{\"error\":\"Required fields are missing\"}");
					}

					spaceMarine.setName(formData.getName());
					spaceMarine.setCoordinates_x(formData.getCoordinates().getX());
					spaceMarine.setCoordinates_y(formData.getCoordinates().getY());
					spaceMarine.setChapter_name(formData.getChapter().getName());
					spaceMarine.setChapter_marinesCount(formData.getChapter().getMarinesCount());
					spaceMarine.setChapter_world(formData.getChapter().getWorld());
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
		String email;
		try {
			validToken = jwtTokenUtil.validateJwtToken(tokenWithoutBearer);
			email = jwtTokenUtil.getEmailFromJwtToken(tokenWithoutBearer);
		} catch (Exception e) {
			return ResponseEntity.status(400).body("{\"error\":\"Ошибка обработки токена: " + e.getMessage() + "\"}");
		}

		if (validToken) {
			return spaceMarineRepository.findById(id).map(spaceMarine -> {
				if (!spaceMarine.getUser_email().equals(email)) {
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

	// Метод для получения объектов SpaceMarine пользователя
	@PostMapping("/user-objects")
	public ResponseEntity<?> getUserObjects(@RequestHeader("Authorization") String token) {
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
			List<SpaceMarine> userMarines = spaceMarineRepository.findByUserEmail(email);
			return ResponseEntity.ok(userMarines);
		} else {
			return ResponseEntity.status(401).body("{\"error\":\"Неверный или просроченный токен\"}");
		}
	}
}
