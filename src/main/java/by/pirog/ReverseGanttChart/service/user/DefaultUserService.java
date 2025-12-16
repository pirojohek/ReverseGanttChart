package by.pirog.ReverseGanttChart.service.user;

import by.pirog.ReverseGanttChart.exception.UserNotFoundException;
import by.pirog.ReverseGanttChart.security.user.CustomUserDetails;
import by.pirog.ReverseGanttChart.storage.entity.UserEntity;
import by.pirog.ReverseGanttChart.storage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;

    @Override
    public Optional<UserEntity> findUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    @Override
    public UserEntity getCurrentUser() {
        var userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();

        return this.userRepository.findByEmail(userDetails.getEmail())
            .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
