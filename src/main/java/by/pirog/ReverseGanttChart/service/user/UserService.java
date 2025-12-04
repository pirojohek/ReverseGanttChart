package by.pirog.ReverseGanttChart.service.user;

import by.pirog.ReverseGanttChart.storage.entity.UserEntity;

import java.util.Optional;

public interface UserService {

    Optional<UserEntity> findUserByEmail(String email);

    UserEntity getCurrentUser();
}
