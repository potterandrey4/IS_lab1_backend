package itmo.andrey.lab1_backend.domain.entitie;

import itmo.andrey.lab1_backend.domain.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "spacemarine_user")
@Data
@NoArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;
	@Column(unique = true, nullable = false)
	private String name;
	@Column(nullable = false)
	private String password;
//	@Enumerated(EnumType.STRING)
//	@Column(nullable = false)
//	private RoleEnum role = RoleEnum.USER;

	public User(String username, String password) {
//	public User(String username, String password, RoleEnum role) {
		this.name = username;
		this.password = password;
//		this.role = RoleEnum.USER;
	}
}
