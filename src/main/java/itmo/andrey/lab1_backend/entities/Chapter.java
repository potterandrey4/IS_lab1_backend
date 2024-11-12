package itmo.andrey.lab1_backend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "chapter")
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false, name="user_email")
    private String userEmail;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String count;
    @Column(nullable = false)
    private String world;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
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
