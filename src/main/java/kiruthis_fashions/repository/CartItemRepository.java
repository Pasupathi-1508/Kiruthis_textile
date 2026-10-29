package kiruthis_fashions.repository;

import kiruthis_fashions.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findBySessionTokenOrderByIdAsc(String sessionToken);
    List<CartItem> findByUserIdOrderByIdAsc(Long userId);
    Optional<CartItem> findBySessionTokenAndVariantId(String sessionToken, Long variantId);
    void deleteBySessionToken(String sessionToken);
}
