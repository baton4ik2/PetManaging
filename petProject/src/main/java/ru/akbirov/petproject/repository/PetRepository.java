package ru.akbirov.petproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.akbirov.petproject.entity.Pet;
import ru.akbirov.petproject.entity.PetType;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    
    List<Pet> findByOwnerId(Long ownerId);
    
    List<Pet> findByType(PetType type);
    
    List<Pet> findByOwnerIdAndType(Long ownerId, PetType type);
    
    @Query("SELECT p FROM Pet p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(p.breed) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Pet> searchByNameOrBreed(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT p FROM Pet p JOIN p.owner o WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(p.breed) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(o.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(o.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Pet> search(@Param("searchTerm") String searchTerm);
}

