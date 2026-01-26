package ru.practicum.stats.analyzer.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.stats.analyzer.model.UserEventAction;
import ru.practicum.stats.analyzer.model.UserEventActionId;

import java.util.Collection;
import java.util.List;

public interface UserEventActionRepository extends JpaRepository<UserEventAction, UserEventActionId> {
    List<UserEventAction> findByUserId(long userId);

    List<UserEventAction> findByUserIdOrderByLastActionTimeDesc(long userId, Pageable pageable);

    List<UserEventAction> findByUserIdAndEventIdIn(long userId, Collection<Long> eventIds);

    @Query("""
            select e.eventId as eventId, sum(e.weight) as score
            from UserEventAction e
            where e.eventId in :eventIds
            group by e.eventId
            """)
    List<EventWeightSumView> sumWeightsByEventIds(@Param("eventIds") Collection<Long> eventIds);

    interface EventWeightSumView {
        long getEventId();
        double getScore();
    }
}
