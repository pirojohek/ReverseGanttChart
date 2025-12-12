package by.pirog.ReverseGanttChart.storage.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashSet;
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


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_project_id")
    private ProjectEntity project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_project_component_parent_id")
    private ProjectComponentEntity projectComponentParent;

    @OneToMany(mappedBy = "projectComponentParent", cascade = CascadeType.ALL)
    @BatchSize(size = 50)
    private Set<ProjectComponentEntity> projectComponentChildren = new HashSet<>();

    @Column(name = "c_created_at")
    private Instant createdAt;

    @Column(name = "c_pos")
    private Long pos;

    @Column(name = "c_deadline")
    private Instant deadline;

    @Column(name = "c_start_date")
    private Instant startDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_creator_id")
    private ProjectMembershipEntity creator;

    @OneToOne(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private StudentTaskStatusEntity studentTaskStatus;

    @OneToOne(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ReviewerTaskStatusEntity reviewerTaskStatus;

    @OneToMany(mappedBy = "projectComponent", cascade = CascadeType.ALL)
    @BatchSize(size = 50)
    private Set<TaskMakerEntity> taskMakers = new HashSet<>();

    @OneToMany(mappedBy = "projectComponent", cascade = CascadeType.ALL)
    @BatchSize(size = 50)
    private Set<CommentEntity> comments = new HashSet<>();

    // Методы для работы с датами и добавления taskMaker
    public void setDeadlineFromLocalDate(LocalDate date) {
        this.deadline = date == null ? null : date.atStartOfDay(ZoneOffset.UTC).toInstant();
    }

    public void setStartDateFromLocalDate(LocalDate date) {
        this.startDate = date == null ? null : date.atStartOfDay(ZoneOffset.UTC).toInstant();
    }

    public LocalDate getDeadlineAsLocalDate() {
        return deadline == null ? null : LocalDate.ofInstant(deadline, ZoneOffset.UTC);
    }

    public LocalDate getStartDateAsLocalDate() {
        return startDate == null ? null : LocalDate.ofInstant(startDate, ZoneOffset.UTC);
    }

    public LocalDate getCreatedAtAsLocalDate() {
        return createdAt == null ? null : LocalDate.ofInstant(createdAt, ZoneOffset.UTC);
    }

    public void addTaskMaker(TaskMakerEntity taskMaker){
        if (taskMakers == null) {
            taskMakers = new HashSet<>();
        }
        this.taskMakers.add(taskMaker);
    }
}


