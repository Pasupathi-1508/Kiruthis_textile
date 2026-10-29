package kiruthis_fashions.repository;

import kiruthis_fashions.entity.OrderTrackingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderTrackingEventRepository extends JpaRepository<OrderTrackingEvent, Long> {
    List<OrderTrackingEvent> findByOrderIdOrderByEventTimestampAsc(Long orderId);
}
