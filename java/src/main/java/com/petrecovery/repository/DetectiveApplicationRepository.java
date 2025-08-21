package com.petrecovery.repository;

import com.petrecovery.entity.DetectiveApplication;
import com.petrecovery.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DetectiveApplicationRepository extends JpaRepository<DetectiveApplication, Long> {
    Optional<DetectiveApplication> findTopByUserOrderByCreatedAtDesc(User user);
    List<DetectiveApplication> findByUser(User user);
    
    /**
     * 根据用户ID和状态检查是否存在申请记录
     */
    boolean existsByUserIdAndStatus(Long userId, String status);
}


