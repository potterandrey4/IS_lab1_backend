package itmo.andrey.lab1_backend.repositories;

import itmo.andrey.lab1_backend.entities.SpaceMarine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpaceMarineRepository extends JpaRepository<SpaceMarine, Long> {
	List<SpaceMarine> findByUserEmail(String userEmail);
	SpaceMarine findSpaceMarineById(Long id);
}
