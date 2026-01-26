package ru.practicum.stats.analyzer.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.stats.analyzer.service.RecommendationService;
import ru.practicum.stats.analyzer.service.RecommendedEvent;
import ru.practicum.stats.service.dashboard.InteractionsCountRequestProto;
import ru.practicum.stats.service.dashboard.RecommendationsControllerGrpc;
import ru.practicum.stats.service.dashboard.RecommendedEventProto;
import ru.practicum.stats.service.dashboard.SimilarEventsRequestProto;
import ru.practicum.stats.service.dashboard.UserInteractionsRequestProto;
import ru.practicum.stats.service.dashboard.UserPredictionsRequestProto;

import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class RecommendationsGrpcService extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {
    private final RecommendationService recommendationService;

    @Override
    public void getRecommendationsForUser(UserPredictionsRequestProto request,
                                          StreamObserver<RecommendedEventProto> responseObserver) {
        List<RecommendedEvent> recommendations = recommendationService.getRecommendationsForUser(
                request.getUserId(),
                request.getMaxResults()
        );
        sendRecommendations(recommendations, responseObserver);
    }

    @Override
    public void getSimilarEvents(SimilarEventsRequestProto request,
                                 StreamObserver<RecommendedEventProto> responseObserver) {
        List<RecommendedEvent> recommendations = recommendationService.getSimilarEvents(
                request.getEventId(),
                request.getUserId(),
                request.getMaxResults()
        );
        sendRecommendations(recommendations, responseObserver);
    }

    @Override
    public void getInteractionsCount(InteractionsCountRequestProto request,
                                     StreamObserver<RecommendedEventProto> responseObserver) {
        List<RecommendedEvent> interactions = recommendationService.getInteractionsCount(request.getEventIdList());
        sendRecommendations(interactions, responseObserver);
    }

    @Override
    public void getUserInteractions(UserInteractionsRequestProto request,
                                    StreamObserver<RecommendedEventProto> responseObserver) {
        List<RecommendedEvent> interactions = recommendationService.getUserInteractions(
                request.getUserId(),
                request.getEventIdList()
        );
        sendRecommendations(interactions, responseObserver);
    }

    private void sendRecommendations(List<RecommendedEvent> recommendations,
                                     StreamObserver<RecommendedEventProto> responseObserver) {
        for (RecommendedEvent recommendation : recommendations) {
            responseObserver.onNext(RecommendedEventProto.newBuilder()
                    .setEventId(recommendation.eventId())
                    .setScore(recommendation.score())
                    .build());
        }
        responseObserver.onCompleted();
    }
}
