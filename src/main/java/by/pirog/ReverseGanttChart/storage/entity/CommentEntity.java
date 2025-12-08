package by.pirog.ReverseGanttChart.storage.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(schema = "storage", name = "t_comment")
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(name = "c_comment")
    private String comment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "c_commenter")
    private ProjectMembershipEntity commenter;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "c_project_component_id")
    private ProjectComponentEntity projectComponent;

    @Column(name = "c_created_at")
    private Instant createdAt;

    @Column(name = "c_updated_at")
    private Instant updatedAt;
}
