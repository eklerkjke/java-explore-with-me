package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserService {

    List<UserDto> getUsers(List<Long> userIds, Integer from, Integer size);

    UserDto create(UserDto userDto);

    void delete(Long id);

    User getUserById(Long id);
}