package com.clinical.dms.repository;

import com.clinical.dms.model.DoseOrder;
import com.clinical.dms.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoseOrderRepository extends JpaRepository<DoseOrder, String> {
    Optional<DoseOrder> findByOrderNumber(String orderNumber);
    List<DoseOrder> findByStatus(OrderStatus status);
}
