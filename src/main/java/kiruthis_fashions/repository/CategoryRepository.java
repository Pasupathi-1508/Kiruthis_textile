package kiruthis_fashions.repository;

import kiruthis_fashions.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByIsActiveTrueOrderByDisplayOrderAsc();
    List<Category> findBySegmentAndIsActiveTrueOrderByDisplayOrderAsc(String segment);
    Optional<Category> findBySlug(String slug);
}
