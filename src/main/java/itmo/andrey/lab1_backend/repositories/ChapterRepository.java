package itmo.andrey.lab1_backend.repositories;

import itmo.andrey.lab1_backend.entities.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
}
