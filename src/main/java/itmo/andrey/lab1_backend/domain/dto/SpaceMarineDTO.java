package itmo.andrey.lab1_backend.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class SpaceMarineDTO {

	@NotBlank(message = "Имя не может быть пустым")
	@NotNull(message = "Имя не может быть null")
	private String name;

	@NotNull(message = "Координаты не могут быть null")
	private CoordinatesDTO coordinates;

	@NotNull(message = "Chapter не может быть null")
	private ChapterDTO chapter;

	@NotNull(message = "Health не может быть null")
	@Positive(message = "Health должен быть больше 0")
	private BigInteger health;

	private BigDecimal height;

	@NotNull(message = "Category не может быть null")
	private AstartesCategory category;

	@NotNull(message = "WeaponType не может быть null")
	private WeaponType weaponType;


	public String getCategory() {
		return category.getCategory();
	}

	public String getWeaponType() {
		return weaponType.getType();
	}
}

@Getter
enum WeaponType {
	HEAVY_BOLTGUN ("Heavy Boltgun"),
	BOLT_PISTOL ("Bolt Pistol"),
	BOLT_RIFLE ("Bolt Rifle"),
	COMBI_FLAMER ("Combi Flamer"),
	GRAVY_GUN ("Gravy Gun");

	private final String type;
	private WeaponType(String type) {
		this.type = type;
	}
}

@Getter
enum AstartesCategory {
	SCOUT ("Scout"),
	AGGRESSOR ("Aggressor"),
	INCEPTOR ("Inceptor"),
	SUPPRESSOR ("Suppressor"),
	TERMINATOR ("Terminator");

	private final String category;
	private AstartesCategory(String category) {
		this.category = category;
	}
}