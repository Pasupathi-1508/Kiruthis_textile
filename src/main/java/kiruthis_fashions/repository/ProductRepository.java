package kiruthis_fashions.repository;

import kiruthis_fashions.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByIsActiveTrueOrderByIdAsc();
    List<Product> findBySegmentAndIsActiveTrueOrderByIdAsc(String segment);
    List<Product> findByBrandNameIgnoreCaseAndIsActiveTrue(String brandName);
    Optional<Product> findBySlug(String slug);
}