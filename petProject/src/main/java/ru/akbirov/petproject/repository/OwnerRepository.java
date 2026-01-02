package ru.akbirov.petproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.akbirov.petproject.entity.Owner;

import java.util.List;
import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {
    
    Optional<Owner> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT o FROM Owner o WHERE " +
           "LOWER(o.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(o.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(o.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Owner> search(@Param("searchTerm") String searchTerm);
    
    Optional<Owner> findByUserId(Long userId);
}

