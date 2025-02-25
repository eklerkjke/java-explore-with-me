package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.exception.BadTimeException;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.Stats;
import ru.practicum.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {

    private final StatsMapper statsMapper;
    private final StatRepository statRepository;

    @Override
    public StatsDto saveRequest(HitDto hitDto) {
        log.info("Сохранение запроса со статистикой в приложении статистики: {}", hitDto);
        Stats stat = statsMapper.toEntity(hitDto);
        return statsMapper.toDto(statRepository.save(stat));
    }

    @Override
    public List<StatsDto> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (end == null || start == null) {
            throw new BadTimeException("The start and the end date cannot be null");
        }
        if (end.isBefore(start)) {
            throw new BadTimeException("The end date cannot be earlier than the start date");
        }

        log.info("Получение статистики по параметрам: {}, {}, {}, {}", start, end, uris, unique);

        List<StatsDto> list;

        if (uris.isEmpty()) {
            if (unique) {
                list = statRepository.getStatsWithoutUriWithUniqueIp(start, end);
            } else {
                list = statRepository.getStatsWithoutUri(start, end);
            }
        } else {
            if (unique) {
                list = statRepository.getStatWithUriWithUniqueIp(start, end, uris);
            } else {
                list = statRepository.getStatsWithUri(start, end, uris);
            }
        }

        log.info("Статистика для запроса: {}", list);

        return list;
    }
}
