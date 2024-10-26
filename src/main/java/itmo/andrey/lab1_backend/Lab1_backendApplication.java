package itmo.andrey.lab1_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("itmo.andrey.lab1_backend.repositories")
public class Lab1_backendApplication {

	public static void main(String[] args) {
		SpringApplication.run(Lab1_backendApplication.class, args);
	}

}
