package itmo.andrey.lab1_backend.forms;

import java.math.BigDecimal;

public class ChapterForm {
    private String id;
    private String name; //Поле не может быть null, Строка не может быть пустой
    private String marinesCount; //Поле не может быть null, Значение поля должно быть больше 0, Максимальное значение поля: 1000
    private String world; //Поле может быть null

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMarinesCount() {
        return marinesCount;
    }

    public void setMarinesCount(String marinesCount) {
        this.marinesCount = marinesCount;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }
}
