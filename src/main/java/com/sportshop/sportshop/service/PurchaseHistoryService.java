package com.sportshop.sportshop.service;

import com.sportshop.sportshop.model.PurchaseHistory;
import com.sportshop.sportshop.model.User;
import com.sportshop.sportshop.repository.PurchaseHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseHistoryService {

    private final PurchaseHistoryRepository purchaseHistoryRepository;

    @Autowired
    public PurchaseHistoryService(PurchaseHistoryRepository purchaseHistoryRepository) {
        this.purchaseHistoryRepository = purchaseHistoryRepository;
    }

    public List<PurchaseHistory> findByUser(User user) {
        return purchaseHistoryRepository.findByUserId(user.getId());
    }


    public PurchaseHistory save(PurchaseHistory purchaseHistory) {
        return purchaseHistoryRepository.save(purchaseHistory);
    }
}
