package ru.practicum.stats.collector.service;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.collector.kafka.UserActionProducer;
import ru.practicum.stats.service.collector.UserActionControllerGrpc;
import ru.practicum.stats.service.collector.UserActionProto;

import java.time.Instant;

@GrpcService
@RequiredArgsConstructor
public class UserActionGrpcService extends UserActionControllerGrpc.UserActionControllerImplBase {
    private final UserActionProducer producer;
    private final ActionTypeMapper actionTypeMapper;

    @Override
    public void collectUserAction(UserActionProto request, StreamObserver<Empty> responseObserver) {
        Instant actionTime = Instant.ofEpochSecond(request.getTimestamp().getSeconds(),
                request.getTimestamp().getNanos());

        UserActionAvro action = UserActionAvro.newBuilder()
                .setUserId(request.getUserId())
                .setEventId(request.getEventId())
                .setActionType(actionTypeMapper.toAvro(request.getActionType()))
                .setTimestamp(actionTime)
                .build();

        producer.send(action);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
