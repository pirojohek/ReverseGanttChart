package by.pirog.ReverseGanttChart.storage.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(schema = "storage", name = "t_task_maker")
public class TaskMakerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_membership_id")
    private ProjectMembershipEntity membership;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_project_component_id")
    private ProjectComponentEntity projectComponent;
}
