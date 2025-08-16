package com.petrecovery.service;

import com.petrecovery.entity.LostPet;
import com.petrecovery.entity.User;
import com.petrecovery.repository.LostPetRepository;
import com.petrecovery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LostPetService {

    @Autowired
    private LostPetRepository lostPetRepository;

    @Autowired
    private UserRepository userRepository;

    public LostPet createLostPet(LostPet lostPet, Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            lostPet.setUser(userOptional.get());
            lostPet.setStatus("lost");
            return lostPetRepository.save(lostPet);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public List<LostPet> getAllLostPets() {
        // 首页仅展示已审核通过的数据
        return lostPetRepository.findByStatus("approved");
    }

    public List<LostPet> getAllLostPets(String petType, String city) {
        // 如果没有筛选条件，返回所有丢失状态的宠物
        if ((petType == null || petType.isEmpty() || "all".equals(petType)) && 
            (city == null || city.isEmpty() || "all".equals(city))) {
            return lostPetRepository.findByStatus("approved");
        }
        
        // 根据筛选条件查询
        return lostPetRepository.findByFilters("approved", petType, city);
    }

    public List<LostPet> getUserLostPets(Long userId) {
        return lostPetRepository.findByUserId(userId);
    }

    public Optional<LostPet> getLostPetById(Long id) {
        return lostPetRepository.findById(id);
    }

    public LostPet updateLostPet(Long id, LostPet updatedLostPet) {
        Optional<LostPet> existing = lostPetRepository.findById(id);
        if (existing.isPresent()) {
            LostPet lostPet = existing.get();
            lostPet.setPetName(updatedLostPet.getPetName());
            lostPet.setPetType(updatedLostPet.getPetType());
            lostPet.setPetBreed(updatedLostPet.getPetBreed());
            lostPet.setPetDescription(updatedLostPet.getPetDescription());
            lostPet.setLostLocation(updatedLostPet.getLostLocation());
            lostPet.setLostTime(updatedLostPet.getLostTime());
            lostPet.setContactInfo(updatedLostPet.getContactInfo());
            lostPet.setReward(updatedLostPet.getReward());
            lostPet.setImages(updatedLostPet.getImages());
            lostPet.setStatus(updatedLostPet.getStatus());
            return lostPetRepository.save(lostPet);
        } else {
            throw new RuntimeException("LostPet not found");
        }
    }

    public void deleteLostPet(Long id) {
        lostPetRepository.deleteById(id);
    }
}