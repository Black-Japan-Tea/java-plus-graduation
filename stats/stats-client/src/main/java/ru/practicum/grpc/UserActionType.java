package ru.practicum.grpc;

import ru.practicum.stats.service.collector.ActionTypeProto;

public enum UserActionType {
    VIEW,
    REGISTER,
    LIKE;

    public ActionTypeProto toProto() {
        return switch (this) {
            case VIEW -> ActionTypeProto.ACTION_VIEW;
            case REGISTER -> ActionTypeProto.ACTION_REGISTER;
            case LIKE -> ActionTypeProto.ACTION_LIKE;
        };
    }
}
