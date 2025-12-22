package ru.akbirov.petproject.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.akbirov.petproject.dto.PetRequestDto;
import ru.akbirov.petproject.dto.PetResponseDto;
import ru.akbirov.petproject.entity.Pet;

@Mapper(componentModel = "spring")
public interface PetMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Pet toEntity(PetRequestDto dto);
    
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "ownerName", expression = "java(pet.getOwner().getFirstName() + \" \" + pet.getOwner().getLastName())")
    PetResponseDto toResponseDto(Pet pet);
}

