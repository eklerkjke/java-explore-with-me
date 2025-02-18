package ru.practicum.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.model.Stats;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StatsMapper {
    StatsDto toDto(Stats stats);

    Stats toEntity(HitDto hitDto);
}
