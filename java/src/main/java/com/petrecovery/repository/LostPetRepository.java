package com.petrecovery.repository;

import com.petrecovery.entity.LostPet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LostPetRepository extends JpaRepository<LostPet, Long> {
    List<LostPet> findByUserId(Long userId);
    List<LostPet> findByStatus(String status);
    
    /**
     * 根据状态、宠物类型和城市筛选宠物信息
     * 使用JPQL查询，支持动态筛选条件
     */
    @Query("SELECT l FROM LostPet l WHERE l.status = :status " +
           "AND (:petType IS NULL OR :petType = '' OR :petType = 'all' OR l.petType = :petType) " +
           "AND (:city IS NULL OR :city = '' OR :city = 'all' OR l.city = :city OR l.lostLocation LIKE %:city%) " +
           "ORDER BY l.createdAt DESC")
    List<LostPet> findByFilters(@Param("status") String status, 
                               @Param("petType") String petType, 
                               @Param("city") String city);
}