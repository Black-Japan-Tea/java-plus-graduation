package ru.practicum.grpc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.practicum.stats.service.dashboard.InteractionsCountRequestProto;
import ru.practicum.stats.service.dashboard.RecommendationsControllerGrpc;
import ru.practicum.stats.service.dashboard.RecommendedEventProto;
import ru.practicum.stats.service.dashboard.SimilarEventsRequestProto;
import ru.practicum.stats.service.dashboard.UserInteractionsRequestProto;
import ru.practicum.stats.service.dashboard.UserPredictionsRequestProto;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationsClient {

    @GrpcClient("analyzer")
    private RecommendationsControllerGrpc.RecommendationsControllerBlockingStub client;

    public List<RecommendedEvent> getRecommendationsForUser(long userId, int maxResults) {
        UserPredictionsRequestProto request = UserPredictionsRequestProto.newBuilder()
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();

        try {
            return asStream(client.getRecommendationsForUser(request))
                    .map(this::toRecommendedEvent)
                    .toList();
        } catch (Exception ex) {
            log.warn("Failed to fetch recommendations for user {}", userId, ex);
            return List.of();
        }
    }

    public List<RecommendedEvent> getSimilarEvents(long eventId, long userId, int maxResults) {
        SimilarEventsRequestProto request = SimilarEventsRequestProto.newBuilder()
                .setEventId(eventId)
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();

        try {
            return asStream(client.getSimilarEvents(request))
                    .map(this::toRecommendedEvent)
                    .toList();
        } catch (Exception ex) {
            log.warn("Failed to fetch similar events for event {} and user {}", eventId, userId, ex);
            return List.of();
        }
    }

    public Map<Long, Double> getInteractionsCount(Collection<Long> eventIds) {
        InteractionsCountRequestProto request = InteractionsCountRequestProto.newBuilder()
                .addAllEventId(eventIds)
                .build();

        try {
            return asStream(client.getInteractionsCount(request))
                    .map(this::toRecommendedEvent)
                    .collect(Collectors.toMap(RecommendedEvent::eventId, RecommendedEvent::score));
        } catch (Exception ex) {
            log.warn("Failed to fetch interactions count for events {}", eventIds, ex);
            return Map.of();
        }
    }

    public Map<Long, Double> getUserInteractions(long userId, Collection<Long> eventIds) {
        UserInteractionsRequestProto request = UserInteractionsRequestProto.newBuilder()
                .setUserId(userId)
                .addAllEventId(eventIds)
                .build();

        try {
            return asStream(client.getUserInteractions(request))
                    .map(this::toRecommendedEvent)
                    .collect(Collectors.toMap(RecommendedEvent::eventId, RecommendedEvent::score));
        } catch (Exception ex) {
            log.warn("Failed to fetch user interactions for user {}", userId, ex);
            return Map.of();
        }
    }

    private RecommendedEvent toRecommendedEvent(RecommendedEventProto proto) {
        return new RecommendedEvent(proto.getEventId(), proto.getScore());
    }

    private Stream<RecommendedEventProto> asStream(Iterator<RecommendedEventProto> iterator) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                iterator, Spliterator.ORDERED), false);
    }
}
