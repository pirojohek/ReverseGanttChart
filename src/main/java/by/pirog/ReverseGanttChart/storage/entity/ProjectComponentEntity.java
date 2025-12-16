package by.pirog.ReverseGanttChart.storage.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.*;
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

    @Column(name = "c_summary_status")
    private

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
    public void setDeadlineFromLocalDateAndTime(LocalDate date, LocalTime time){
        this.deadline = Instant.from(LocalDateTime.of(date, time));
    }

    public void setDeadlineFromLocalDate(LocalDate localDate){
        this.deadline = Instant.from(LocalDateTime.of(localDate, LocalTime.MIN));
    }

    public void setStartDateFromLocalDate(LocalDate date){
        this.startDate = Instant.from(LocalDateTime.of(date, LocalTime.MIN));
    }

    public void setStartDateFromLocalDateAndTime(LocalDate date, LocalTime time){
        this.startDate = Instant.from(LocalDateTime.of(date, time));
    }

    public LocalDateTime getCreatedAtAsLocalDateTime() {
        return createdAt != null
                ? LocalDateTime.ofInstant(createdAt, ZoneId.systemDefault())
                : null;
    }

    public LocalDateTime getStartDateAsLocalDateTime(){
        return startDate != null
                ? LocalDateTime.ofInstant(startDate, ZoneId.systemDefault())
                : null;
    }

    public LocalDateTime getDeadlineAsLocalDateTime(){
        return deadline != null
                ? LocalDateTime.ofInstant(deadline, ZoneId.systemDefault())
                : null;
    }

    public void addTaskMaker(TaskMakerEntity taskMaker){
        if (taskMakers == null) {
            taskMakers = new HashSet<>();
        }
        this.taskMakers.add(taskMaker);
    }
}


