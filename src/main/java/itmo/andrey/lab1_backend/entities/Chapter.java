package itmo.andrey.lab1_backend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "chapter")
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false, name="user_name")
    private String userName;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String count;
    @Column()
    private String world;

    public Chapter() {
    }

    public Chapter(String userName, String name, String count, String world) {
        this.userName = userName;
        this.name = name;
        this.count = count;
        this.world = world;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }
}
