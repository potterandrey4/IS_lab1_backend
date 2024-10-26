package itmo.andrey.lab1_backend.repositories;

import itmo.andrey.lab1_backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	User findByEmail(String email);
	boolean existsByEmail(String email);
}
