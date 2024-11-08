package itmo.andrey.lab1_backend.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.BigInteger;

@Entity
@Table(name = "spacemarine")
public class SpaceMarine {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;
	@Column(nullable = false)
	private String name;
	@Column(nullable = false, name="user_email")
	private String userEmail;
	@Column(nullable = false)
	private BigInteger coordinates_x;
	@Column(nullable = false)
	private BigDecimal coordinates_y;
	@Column(nullable = false)
	private String creationDate;
	@Column(nullable = false)
	private String chapter_name;
	@Column(nullable = false)
	private BigDecimal chapter_marinesCount;
	@Column(nullable = false)
	private String chapter_world;
	@Column(nullable = false)
	private BigInteger health;
	@Column(nullable = false)
	private BigDecimal height;
	@Column(nullable = false)
	private String category;
	@Column(nullable = false)
	private String weaponType;

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigInteger getCoordinates_x() {
		return coordinates_x;
	}

	public void setCoordinates_x(BigInteger coordinates_x) {
		this.coordinates_x = coordinates_x;
	}

	public BigDecimal getCoordinates_y() {
		return coordinates_y;
	}

	public void setCoordinates_y(BigDecimal coordinates_y) {
		this.coordinates_y = coordinates_y;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public String getChapter_name() {
		return chapter_name;
	}

	public void setChapter_name(String chapter_name) {
		this.chapter_name = chapter_name;
	}

	public BigDecimal getChapter_marinesCount() {
		return chapter_marinesCount;
	}

	public void setChapter_marinesCount(BigDecimal chapter_marinesCount) {
		this.chapter_marinesCount = chapter_marinesCount;
	}

	public String getChapter_world() {
		return chapter_world;
	}

	public void setChapter_world(String chapter_world) {
		this.chapter_world = chapter_world;
	}

	public BigInteger getHealth() {
		return health;
	}

	public void setHealth(BigInteger health) {
		this.health = health;
	}

	public BigDecimal getHeight() {
		return height;
	}

	public void setHeight(BigDecimal height) {
		this.height = height;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getWeaponType() {
		return weaponType;
	}

	public void setWeaponType(String weaponType) {
		this.weaponType = weaponType;
	}

	public String getUser_email() {
		return userEmail;
	}

	public void setUser_email(String email) {
		this.userEmail = email;
	}
}
