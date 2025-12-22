package ru.akbirov.petproject.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.akbirov.petproject.dto.OwnerRequestDto;
import ru.akbirov.petproject.dto.OwnerResponseDto;
import ru.akbirov.petproject.entity.Owner;

@Mapper(componentModel = "spring")
public interface OwnerMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pets", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Owner toEntity(OwnerRequestDto dto);
    
    @Mapping(target = "pets", ignore = true)
    OwnerResponseDto toResponseDto(Owner owner);
}

