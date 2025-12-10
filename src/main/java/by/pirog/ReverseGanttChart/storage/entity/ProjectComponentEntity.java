package by.pirog.ReverseGanttChart.storage.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(schema = "storage", name = "t_project_component")
public class ProjectComponentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "c_id")
    private Long id;

    @Column(name = "c_title")
    private String title;

    @Column(name = "c_description")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "c_project_id")
    private ProjectEntity project;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "c_project_component_parent_id")
    private ProjectComponentEntity projectComponentParent;

    @OneToMany(mappedBy = "projectComponentParent", cascade = CascadeType.ALL)
    private Set<ProjectComponentEntity> projectComponentChildren = new HashSet<>();

    @Column(name = "c_created_at")
    private Instant createdAt;

    @Column(name = "c_pos")
    private Long pos;

    @Column(name = "c_deadline")
    private Instant deadline;

    @Column(name = "c_start_date")
    private Instant startDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "c_creator_id")
    private ProjectMembershipEntity creator;

    @OneToOne(mappedBy = "task", cascade = CascadeType.ALL)
    private StudentTaskStatusEntity studentTaskStatus;

    @OneToOne(mappedBy = "task", cascade = CascadeType.ALL)
    private ReviewerTaskStatusEntity reviewerTaskStatus;

    @OneToMany(mappedBy = "projectComponent", cascade = CascadeType.ALL)
    private Set<TaskMakerEntity> taskMakers = new HashSet<>();

    @OneToMany(mappedBy = "projectComponent", cascade = CascadeType.ALL)
    private Set<CommentEntity> comments = new HashSet<>();

    public void setDeadlineFromLocalDate(LocalDate date) {
        if (date == null) {
            this.deadline = null;
        } else {

            this.deadline = date.atStartOfDay(ZoneOffset.UTC).toInstant();
        }
    }

    public void setStartDateFromLocalDate(LocalDate date) {
        if (date == null) {
            this.startDate = null;
        } else {

            this.startDate = date.atStartOfDay(ZoneOffset.UTC).toInstant();
        }
    }

    public LocalDate getDeadlineAsLocalDate() {
        return deadline != null
                ? LocalDate.ofInstant(deadline, ZoneOffset.UTC)
                : null;
    }

    public LocalDate getStartDateAsLocalDate() {
        return startDate != null
                ? LocalDate.ofInstant(startDate, ZoneOffset.UTC)
                : null;
    }

    public LocalDate getCreatedAtAsLocalDate() {
        return createdAt != null
                ? LocalDate.ofInstant(createdAt, ZoneOffset.UTC)
                : null;
    }

    public void addTaskMaker(TaskMakerEntity taskMaker){
        if (taskMakers == null) {
            taskMakers = new HashSet<>();
        }
        this.taskMakers.add(taskMaker);
    }
}
