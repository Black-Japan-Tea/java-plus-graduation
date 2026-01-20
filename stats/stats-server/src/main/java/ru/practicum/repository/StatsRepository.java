package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {


    //Подсчитывает общее количество запросов по каждому app и uri, учитывая все IP-адреса.
    //Фильтрует по временному интервалу и по списку URI (если передан).
    //Сортирует по убыванию количества запросов.
    @Query("SELECT new ru.practicum.ViewStatsDto(h.app, h.uri, COUNT(h.ip)) " +
            "FROM EndpointHit h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "AND (:uris IS NULL OR h.uri IN :uris) " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC")
    List<ViewStatsDto> findAllHits(LocalDateTime start, LocalDateTime end, List<String> uris);

    //Подсчитывает количество уникальных IP-адресов по каждому app и uri.
    //Фильтрует по временному интервалу и по списку URI (если передан).
    //Сортирует по убыванию количества уникальных пользователей.
    @Query("SELECT new ru.practicum.ViewStatsDto(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM EndpointHit h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "AND (:uris IS NULL OR h.uri IN :uris) " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<ViewStatsDto> findUniqueHits(LocalDateTime start, LocalDateTime end, List<String> uris);
}
