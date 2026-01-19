package ru.practicum.ewm.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.enums.State;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestStatusUpdateRequest {
    private List<Long> requestIds;
    private State status;
}
