package by.pirog.ReverseGanttChart.storage.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(schema = "storage", name = "t_project_user_role")
public class ProjectUserRoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "c_id")
    private Long id;

    @Column(name = "c_role_name", unique = true, nullable = false)
    private String roleName;
}
