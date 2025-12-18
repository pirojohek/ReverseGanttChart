package by.pirog.ReverseGanttChart.service.password;

import by.pirog.ReverseGanttChart.dto.passwordDto.NewPasswordDto;
import by.pirog.ReverseGanttChart.exception.InvalidTokenException;
import by.pirog.ReverseGanttChart.service.email.EmailService;
import by.pirog.ReverseGanttChart.service.secret.TokenHashService;
import by.pirog.ReverseGanttChart.storage.entity.ResetPasswordEntity;
import by.pirog.ReverseGanttChart.storage.entity.UserEntity;
import by.pirog.ReverseGanttChart.storage.repository.ResetPasswordRepository;
import by.pirog.ReverseGanttChart.storage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserPasswordService {

    private final ResetPasswordRepository resetPasswordRepository;
    private final UserRepository userRepository;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    @Value("${app.reset-password-ttl}")
    private Duration resetPasswordTtl;

    public void sendResetPassword(String username){
         try{
             Optional<UserEntity> userEntityByUsername = userRepository.findByUsername(username);
             Optional<UserEntity> userEntityByEmail = userRepository.findByEmail(username);

             UserEntity userEntity = null;
             if (userEntityByUsername.isPresent()){
                 userEntity = userEntityByUsername.get();
             } else if (userEntityByEmail.isPresent()){
                 userEntity = userEntityByEmail.get();
             } else {
                 throw new UsernameNotFoundException("Username not found");
             }

             String token = TokenHashService.generateToken(32);
             String hashToken = TokenHashService.hashToken(token);

             ResetPasswordEntity entity = ResetPasswordEntity.builder()
                     .user(userEntity)
                     .hashToken(hashToken)
                     .expiredAt(Instant.now().plus(resetPasswordTtl))
                     .build();

             resetPasswordRepository.save(entity);

             emailService.sendResetPasswordEmail(username, hashToken);

         }catch (Exception e){
            // Todo сюда добавить логи
         }
    }

    public void updatePassword(NewPasswordDto dto){
        String hashToken = TokenHashService.hashToken(dto.token());
        ResetPasswordEntity entity = this.resetPasswordRepository.findResetPasswordEntityByHashToken(hashToken)
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));

        try{
            if (entity.getExpiredAt().isBefore(Instant.now())) {
                throw new InvalidTokenException("Invalid token");
            }
            UserEntity user = entity.getUser();
            user.setPassword(passwordEncoder.encode(dto.password()));
            userRepository.save(user);

        } finally {
            this.resetPasswordRepository.delete(entity);
        }

    }

}
