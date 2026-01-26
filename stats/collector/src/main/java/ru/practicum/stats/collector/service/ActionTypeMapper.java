package ru.practicum.stats.collector.service;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.stats.service.collector.ActionTypeProto;

@Component
public class ActionTypeMapper {

    public ActionTypeAvro toAvro(ActionTypeProto actionType) {
        return switch (actionType) {
            case ACTION_REGISTER -> ActionTypeAvro.REGISTER;
            case ACTION_LIKE -> ActionTypeAvro.LIKE;
            case ACTION_VIEW, UNRECOGNIZED -> ActionTypeAvro.VIEW;
        };
    }
}
