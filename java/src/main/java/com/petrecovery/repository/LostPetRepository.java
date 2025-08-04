package com.petrecovery.repository;

import com.petrecovery.entity.LostPet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LostPetRepository extends JpaRepository<LostPet, Long> {
    List<LostPet> findByUserId(Long userId);
    List<LostPet> findByStatus(String status);
}