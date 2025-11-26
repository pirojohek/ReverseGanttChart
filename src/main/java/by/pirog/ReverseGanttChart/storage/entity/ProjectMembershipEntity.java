package by.pirog.ReverseGanttChart.storage.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(schema = "storage", name = "t_project_membership")
public class ProjectMembershipEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "c_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_user_role")
    private ProjectUserRoleEntity userRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_project")
    private ProjectEntity project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="c_user")
    private UserEntity user;
}
