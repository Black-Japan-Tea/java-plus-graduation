package ru.practicum.grpc;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.practicum.stats.service.collector.UserActionControllerGrpc;
import ru.practicum.stats.service.collector.UserActionProto;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollectorClient {

    @GrpcClient("collector")
    private UserActionControllerGrpc.UserActionControllerBlockingStub client;

    public void collect(long userId, long eventId, UserActionType actionType) {
        Instant now = Instant.now();
        Timestamp timestamp = Timestamp.newBuilder()
                .setSeconds(now.getEpochSecond())
                .setNanos(now.getNano())
                .build();

        UserActionProto request = UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(actionType.toProto())
                .setTimestamp(timestamp)
                .build();

        try {
            client.collectUserAction(request);
        } catch (Exception ex) {
            log.warn("Failed to send user action to collector (userId={}, eventId={}, action={})",
                    userId, eventId, actionType, ex);
        }
    }
}
