package by.pirog.ReverseGanttChart.storage.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(schema = "storage", name = "t_reviewer_task_status")
public class ReviewerTaskStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "c_reviewer_id")
    private ProjectMembershipEntity projectMembership;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "c_project_component_id")
    private ProjectComponentEntity task;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "c_task_status_id")
    private TaskStatusEntity taskStatus;

}
