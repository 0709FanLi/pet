package com.petrecovery.repository;

import com.petrecovery.entity.DetectiveOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DetectiveOrderRepository extends JpaRepository<DetectiveOrder, Long> {
    
    /**
     * 根据侦探ID查询订单列表
     */
    Page<DetectiveOrder> findByDetectiveIdOrderByCreatedAtDesc(Long detectiveId, Pageable pageable);
    
    /**
     * 根据侦探ID和状态查询订单列表
     */
    Page<DetectiveOrder> findByDetectiveIdAndStatusOrderByCreatedAtDesc(Long detectiveId, String status, Pageable pageable);
    
    /**
     * 根据宠物ID查询所有意向订单
     */
    List<DetectiveOrder> findByLostPetIdAndStatusOrderByCreatedAtDesc(Long lostPetId, String status);
    
    /**
     * 查询特定侦探对特定宠物的订单状态
     */
    Optional<DetectiveOrder> findByDetectiveIdAndLostPetId(Long detectiveId, Long lostPetId);
    
    /**
     * 检查侦探是否已经对某个宠物表达过意向或接单
     */
    boolean existsByDetectiveIdAndLostPetId(Long detectiveId, Long lostPetId);
    
    /**
     * 统计某个宠物的意向数量
     */
    long countByLostPetIdAndStatus(Long lostPetId, String status);
    
    /**
     * 查询宠物的已确认订单
     */
    Optional<DetectiveOrder> findByLostPetIdAndStatus(Long lostPetId, String status);
    
    /**
     * 统计侦探的所有订单数量
     */
    long countByDetectiveId(Long detectiveId);
    
    /**
     * 根据侦探ID和状态统计订单数量
     */
    long countByDetectiveIdAndStatus(Long detectiveId, String status);
    
    /**
     * 查询侦探的接单统计
     */
    @Query("SELECT COUNT(do) FROM DetectiveOrder do WHERE do.detectiveId = :detectiveId AND do.status IN ('confirmed', 'in_progress', 'completed')")
    long countAcceptedOrdersByDetectiveId(@Param("detectiveId") Long detectiveId);
    

    
    /**
     * 根据宠物ID和状态批量更新（用于撤回其他侦探的意向）
     */
    @Query("UPDATE DetectiveOrder do SET do.status = 'closed', do.updatedAt = CURRENT_TIMESTAMP WHERE do.lostPetId = :lostPetId AND do.status = 'intention' AND do.detectiveId != :excludeDetectiveId")
    int closeOtherIntentions(@Param("lostPetId") Long lostPetId, @Param("excludeDetectiveId") Long excludeDetectiveId);
}
