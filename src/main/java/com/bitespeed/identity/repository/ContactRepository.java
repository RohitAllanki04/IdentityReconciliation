package com.bitespeed.identity.repository;

import com.bitespeed.identity.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    @Query("SELECT c FROM Contact c WHERE " +
            "(c.email = :email AND :email IS NOT NULL) OR " +
            "(c.phoneNumber = :phone AND :phone IS NOT NULL)")
    List<Contact> findByEmailOrPhone(@Param("email") String email,
                                     @Param("phone") String phone);

    List<Contact> findByLinkedId(Long linkedId);
}