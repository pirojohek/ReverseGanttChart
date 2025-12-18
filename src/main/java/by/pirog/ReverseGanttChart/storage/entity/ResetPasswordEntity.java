package by.pirog.ReverseGanttChart.storage.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@Entity
@Table(name = "t_reset_password", schema = "storage")
public class ResetPasswordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "c_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "c_user_id")
    private UserEntity user;

    @Column(name = "c_hash_token")
    private String hashToken;

    @Column(name = "c_expired_at")
    private Instant expiredAt;
}
