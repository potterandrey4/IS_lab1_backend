package itmo.andrey.lab1_backend.forms;

import itmo.andrey.lab1_backend.models.Chapter;
import itmo.andrey.lab1_backend.models.Coordinates;

import java.math.BigDecimal;
import java.math.BigInteger;

public class SpaceMarineForm {
	private String name;
	private Coordinates coordinates;
	private ChapterForm chapter;
	private String health;
	private String height;
	private String category;
	private String weaponType;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Coordinates getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
	}

	public ChapterForm getChapter() {
		return chapter;
	}

	public void setChapter(ChapterForm chapter) {
		this.chapter = chapter;
	}

	public BigInteger getHealth() {
		return new BigInteger(health);
	}

	public void setHealth(String health) {
		this.health = health;
	}

	public BigDecimal getHeight() {
		return new BigDecimal(height);
	}

	public void setHeight(String height) {
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
}
