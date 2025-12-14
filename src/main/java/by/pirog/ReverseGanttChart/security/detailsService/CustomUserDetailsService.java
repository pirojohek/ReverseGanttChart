package by.pirog.ReverseGanttChart.security.detailsService;

import by.pirog.ReverseGanttChart.security.user.CustomUserDetails;
import by.pirog.ReverseGanttChart.storage.entity.UserEntity;
import by.pirog.ReverseGanttChart.storage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    public final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Todo - тут доделать проверку, либо по логину либо по почте
        Optional<UserEntity> userEntityByEmail = userRepository.findByEmail(usernameOrEmail);
        Optional<UserEntity> userEntityByUsername = userRepository.findByUsername(usernameOrEmail);

        if (userEntityByUsername.isPresent()) {
            return new CustomUserDetails(userEntityByUsername.get());
        } else if (userEntityByEmail.isPresent()) {
            return new CustomUserDetails(userEntityByEmail.get());
        }
        throw new UsernameNotFoundException("Login or password is incorrect");

    }
}
