package com.sportshop.sportshop.repository;

import com.sportshop.sportshop.model.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserHistoryRepository extends JpaRepository<UserHistory, Long> {
    List<UserHistory> findByUserId(Long userId);

    @Query("SELECT uh FROM UserHistory uh " +
            "WHERE (:userId IS NULL OR uh.userId = :userId) " +
            "AND (:action IS NULL OR uh.action LIKE %:action%) " +
            "AND (:dateFrom IS NULL OR uh.timestamp >= :dateFrom) " +
            "AND (:dateTo IS NULL OR uh.timestamp <= :dateTo)")
    List<UserHistory> filterUserHistory(
            @Param("userId") Long userId,
            @Param("action") String action,
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo);
}
