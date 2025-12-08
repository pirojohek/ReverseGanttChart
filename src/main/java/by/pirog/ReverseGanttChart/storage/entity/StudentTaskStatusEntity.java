package by.pirog.ReverseGanttChart.storage.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(schema = "storage", name = "t_student_task_status")
public class StudentTaskStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "c_student_id")
    private ProjectMembershipEntity student;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "c_project_component_id")
    private ProjectComponentEntity task;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "c_task_status_id")
    private TaskStatusEntity status;
}
