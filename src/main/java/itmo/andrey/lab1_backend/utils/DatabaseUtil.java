package itmo.andrey.lab1_backend.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import itmo.andrey.lab1_backend.entities.User;
import itmo.andrey.lab1_backend.repositories.UserRepository;

@Component
public class DatabaseUtil {

	private final UserRepository userRepository;

	@Autowired
	public DatabaseUtil(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public User saveUser(User user) {
		return userRepository.save(user);
	}
}
