package by.pirog.ReverseGanttChart.storage.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "storage", name = "t_project_invite")
public class ProjectInviteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "c_id")
    private Long id;

    @Column(name = "c_token")
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_user_role")
    private ProjectUserRoleEntity userRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_project_id")
    private ProjectEntity project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_inviter")
    private ProjectMembershipEntity inviter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_user_id")
    private UserEntity user;

    @Column(name = "c_created_at")
    private LocalDateTime createdAt;

}
