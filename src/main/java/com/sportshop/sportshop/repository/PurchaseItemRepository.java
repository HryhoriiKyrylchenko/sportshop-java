package com.sportshop.sportshop.repository;

import com.sportshop.sportshop.model.PurchaseItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, Long> {

}
