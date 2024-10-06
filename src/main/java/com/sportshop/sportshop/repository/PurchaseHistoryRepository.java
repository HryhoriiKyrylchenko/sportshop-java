package com.sportshop.sportshop.repository;

import com.sportshop.sportshop.model.PurchaseHistory;
import com.sportshop.sportshop.model.PurchaseItem;
import com.sportshop.sportshop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseHistoryRepository extends JpaRepository<PurchaseHistory, Long> {
    List<PurchaseHistory> findByUserId(Long userId);
}
