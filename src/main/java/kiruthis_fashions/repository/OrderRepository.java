package kiruthis_fashions.repository;

import kiruthis_fashions.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByCustomerPhoneOrderByCreatedAtDesc(String customerPhone);
    List<Order> findByOrderStatusOrderByCreatedAtDesc(String orderStatus);
    List<Order> findAllByOrderByCreatedAtDesc();
}