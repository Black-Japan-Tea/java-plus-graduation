package ru.practicum.stats.analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.stats.analyzer.config.RecommendationProperties;
import ru.practicum.stats.analyzer.model.EventSimilarity;
import ru.practicum.stats.analyzer.model.UserEventAction;
import ru.practicum.stats.analyzer.repository.EventSimilarityRepository;
import ru.practicum.stats.analyzer.repository.UserEventActionRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final EventSimilarityRepository similarityRepository;
    private final UserEventActionRepository actionRepository;
    private final RecommendationProperties recommendationProperties;

    public List<RecommendedEvent> getSimilarEvents(long eventId, long userId, int maxResults) {
        if (maxResults <= 0) {
            return List.of();
        }

        Set<Long> interacted = actionRepository.findByUserId(userId).stream()
                .map(UserEventAction::getEventId)
                .collect(Collectors.toSet());

        List<EventSimilarity> similarities = new ArrayList<>();
        similarities.addAll(similarityRepository.findByEventA(eventId));
        similarities.addAll(similarityRepository.findByEventB(eventId));

        return similarities.stream()
                .map(similarity -> new RecommendedEvent(resolveOtherEvent(similarity, eventId), similarity.getScore()))
                .filter(recommendation -> !interacted.contains(recommendation.eventId()))
                .sorted(Comparator.comparingDouble(RecommendedEvent::score).reversed())
                .limit(maxResults)
                .toList();
    }

    public List<RecommendedEvent> getRecommendationsForUser(long userId, int maxResults) {
        if (maxResults <= 0) {
            return List.of();
        }

        List<UserEventAction> recentActions = actionRepository
                .findByUserIdOrderByLastActionTimeDesc(userId, PageRequest.of(0, maxResults));
        if (recentActions.isEmpty()) {
            return List.of();
        }

        Set<Long> interacted = actionRepository.findByUserId(userId).stream()
                .map(UserEventAction::getEventId)
                .collect(Collectors.toSet());

        Map<Long, Double> candidateScores = new HashMap<>();
        for (UserEventAction action : recentActions) {
            long baseEventId = action.getEventId();
            List<EventSimilarity> similarities = new ArrayList<>();
            similarities.addAll(similarityRepository.findByEventA(baseEventId));
            similarities.addAll(similarityRepository.findByEventB(baseEventId));
            for (EventSimilarity similarity : similarities) {
                long candidateId = resolveOtherEvent(similarity, baseEventId);
                if (interacted.contains(candidateId)) {
                    continue;
                }
                candidateScores.merge(candidateId, similarity.getScore(), Math::max);
            }
        }

        List<Long> candidates = candidateScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(maxResults)
                .map(Map.Entry::getKey)
                .toList();

        List<RecommendedEvent> scoredRecommendations = new ArrayList<>();
        for (Long candidateId : candidates) {
            double predictedScore = predictScore(userId, candidateId, interacted);
            scoredRecommendations.add(new RecommendedEvent(candidateId, predictedScore));
        }

        return scoredRecommendations.stream()
                .sorted(Comparator.comparingDouble(RecommendedEvent::score).reversed())
                .limit(maxResults)
                .toList();
    }

    public List<RecommendedEvent> getInteractionsCount(Collection<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return List.of();
        }

        Map<Long, Double> sums = actionRepository.sumWeightsByEventIds(eventIds).stream()
                .collect(Collectors.toMap(
                        UserEventActionRepository.EventWeightSumView::getEventId,
                        UserEventActionRepository.EventWeightSumView::getScore
                ));

        List<RecommendedEvent> results = new ArrayList<>();
        for (Long eventId : eventIds) {
            results.add(new RecommendedEvent(eventId, sums.getOrDefault(eventId, 0.0)));
        }
        return results;
    }

    public List<RecommendedEvent> getUserInteractions(long userId, Collection<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return List.of();
        }

        Map<Long, Double> weights = actionRepository.findByUserIdAndEventIdIn(userId, eventIds).stream()
                .collect(Collectors.toMap(UserEventAction::getEventId, UserEventAction::getWeight));

        List<RecommendedEvent> results = new ArrayList<>();
        for (Long eventId : eventIds) {
            results.add(new RecommendedEvent(eventId, weights.getOrDefault(eventId, 0.0)));
        }
        return results;
    }

    private double predictScore(long userId, long candidateId, Set<Long> interacted) {
        int neighborsCount = Math.max(0, recommendationProperties.neighborsCount());

        List<EventSimilarity> similarities = new ArrayList<>();
        similarities.addAll(similarityRepository.findByEventA(candidateId));
        similarities.addAll(similarityRepository.findByEventB(candidateId));

        List<RecommendedEvent> neighbors = similarities.stream()
                .map(similarity -> new RecommendedEvent(resolveOtherEvent(similarity, candidateId), similarity.getScore()))
                .filter(recommendation -> interacted.contains(recommendation.eventId()))
                .sorted(Comparator.comparingDouble(RecommendedEvent::score).reversed())
                .limit(neighborsCount)
                .toList();

        if (neighbors.isEmpty()) {
            return 0.0;
        }

        Set<Long> neighborIds = neighbors.stream()
                .map(RecommendedEvent::eventId)
                .collect(Collectors.toSet());

        Map<Long, Double> weights = actionRepository.findByUserIdAndEventIdIn(userId, neighborIds).stream()
                .collect(Collectors.toMap(UserEventAction::getEventId, UserEventAction::getWeight));

        double weightedSum = 0.0;
        double similaritySum = 0.0;
        for (RecommendedEvent neighbor : neighbors) {
            double weight = weights.getOrDefault(neighbor.eventId(), 0.0);
            weightedSum += weight * neighbor.score();
            similaritySum += neighbor.score();
        }

        if (similaritySum == 0.0) {
            return 0.0;
        }
        return weightedSum / similaritySum;
    }

    private long resolveOtherEvent(EventSimilarity similarity, long eventId) {
        return similarity.getEventA() == eventId ? similarity.getEventB() : similarity.getEventA();
    }
}
