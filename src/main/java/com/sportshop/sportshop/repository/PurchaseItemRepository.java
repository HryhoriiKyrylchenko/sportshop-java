package com.sportshop.sportshop.repository;

import com.sportshop.sportshop.model.PurchaseHistory;
import com.sportshop.sportshop.model.PurchaseItem;
import com.sportshop.sportshop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, Long> {

}
