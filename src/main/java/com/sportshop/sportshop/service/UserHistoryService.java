package com.sportshop.sportshop.service;

import com.sportshop.sportshop.model.UserHistory;
import com.sportshop.sportshop.repository.UserHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserHistoryService {

    private final UserHistoryRepository userHistoryRepository;

    @Autowired
    public UserHistoryService(UserHistoryRepository userHistoryRepository) {
        this.userHistoryRepository = userHistoryRepository;
    }

    public void logUserAction(Long userId, String action) {
        UserHistory userHistory = new UserHistory();
        userHistory.setUserId(userId);
        userHistory.setAction(action);
        userHistory.setTimestamp(LocalDateTime.now());
        userHistoryRepository.save(userHistory);
    }

    public List<UserHistory> getAllUserHistory() {
        return userHistoryRepository.findAll();
    }

    public List<UserHistory> filterUserHistory(Long userId, String action, LocalDateTime dateFrom, LocalDateTime dateTo) {
        return userHistoryRepository.filterUserHistory(userId, action, dateFrom, dateTo);
    }
}
