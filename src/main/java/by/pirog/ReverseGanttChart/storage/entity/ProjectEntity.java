package by.pirog.ReverseGanttChart.storage.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(schema = "storage", name = "t_project")
public class ProjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "c_id")
    private Long id;

    @Column(name = "c_project_name")
    private String projectName;

    @Column(name = "c_project_description")
    private String projectDescription;
    @Column(name = "c_deadline")
    private Instant deadline;

    @Column(name = "c_created_at")
    private Instant createdAt;

    @Column(name = "c_updated_at")
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "c_project_owner")
    private UserEntity projectOwner;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<ProjectComponentEntity> projectComponents;

    public void setDeadlineFromLocalDate(LocalDate date) {
        if (date == null) {
            this.deadline = null;
        } else {

            this.deadline = date.atStartOfDay(ZoneOffset.UTC).toInstant();
        }
    }

    public LocalDate getDeadlineAsLocalDate() {
        return deadline != null
                ? LocalDate.ofInstant(deadline, ZoneOffset.UTC)
                : null;
    }

    public LocalDate getCreatedAtAsLocalDate() {
        return createdAt != null
                ? LocalDate.ofInstant(createdAt, ZoneOffset.UTC)
                : null;
    }

    public LocalDate getUpdatedAtAsLocalDate() {
        return updatedAt != null
                ? LocalDate.ofInstant(updatedAt, ZoneOffset.UTC)
                : null;
    }
}
