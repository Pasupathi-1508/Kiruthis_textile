package kiruthis_fashions.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_tracking_events")
public class OrderTrackingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;

    @Column(name = "status_code", nullable = false, length = 50)
    private String statusCode;

    @Column(name = "location_hub", nullable = false, length = 150)
    private String locationHub;

    @Column(nullable = false, length = 500)
    private String remarks;

    @Column(name = "event_timestamp", insertable = false, updatable = false)
    private LocalDateTime eventTimestamp;

    public OrderTrackingEvent() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public String getStatusCode() { return statusCode; }
    public void setStatusCode(String statusCode) { this.statusCode = statusCode; }

    public String getLocationHub() { return locationHub; }
    public void setLocationHub(String locationHub) { this.locationHub = locationHub; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getEventTimestamp() { return eventTimestamp; }
    public void setEventTimestamp(LocalDateTime eventTimestamp) { this.eventTimestamp = eventTimestamp; }
}