package by.pirog.ReverseGanttChart.service.registration;

import by.pirog.ReverseGanttChart.dto.RegisteredResponseDto;
import by.pirog.ReverseGanttChart.dto.RegistrationDto;
import by.pirog.ReverseGanttChart.exception.EmailAlreadyExists;
import by.pirog.ReverseGanttChart.exception.UsernameAlreadyExists;
import by.pirog.ReverseGanttChart.storage.entity.UserEntity;
import by.pirog.ReverseGanttChart.storage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultRegisterService implements RegisterService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public RegisteredResponseDto register(RegistrationDto registrationDto) {
        if (userRepository.existsByEmail(registrationDto.email())){
            throw new EmailAlreadyExists("User with email " + registrationDto.email()+ " already exists");
        }
        if (userRepository.existsByUsername(registrationDto.username())){
            throw new UsernameAlreadyExists("User with username " + registrationDto.username() + " already exists");
        }

        userRepository.save(UserEntity.builder()
                        .email(registrationDto.email())
                        .username(registrationDto.username())
                        .password(passwordEncoder.encode(registrationDto.password()))
                .build());

        return RegisteredResponseDto.builder()
                .email(registrationDto.email())
                .username(registrationDto.username())
                .build();
    }
}
