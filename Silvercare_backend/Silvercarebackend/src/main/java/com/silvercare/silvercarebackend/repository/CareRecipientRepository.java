package com.silvercare.silvercarebackend.repository;

import com.silvercare.silvercarebackend.domain.CareRecipient;
import com.silvercare.silvercarebackend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CareRecipientRepository extends JpaRepository<CareRecipient, Long> {
    List<CareRecipient> findByActiveTrue();
    Optional<CareRecipient> findByPhoneNumber(String phoneNumber);
    List<CareRecipient> findByCreatedBy(User user);
    List<CareRecipient> findByUsersContaining(User user);
    List<CareRecipient> findByFamilyId(Long familyId);
}
