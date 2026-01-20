package ru.practicum.ewm.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

@FeignClient(name = "user-service")
public interface UsersClient {
    @GetMapping("/admin/users")
    List<UserDto> getUsers(@RequestParam("ids") Long[] ids);
}
