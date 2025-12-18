package by.pirog.ReverseGanttChart.controller;

import by.pirog.ReverseGanttChart.dto.projectDto.*;
import by.pirog.ReverseGanttChart.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/info")
    public ResponseEntity<ProjectInfoDto> getProjectInfo(){
        ProjectInfoDto response = projectService.getProjectInfo();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<CreatedProjectDto> createProject(@RequestBody CreateProjectDto createProjectDto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(createProjectDto));
    }

    @DeleteMapping("/action/delete")
    public ResponseEntity<Void> deleteProject(){
        this.projectService.deleteProject();

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/action/update")
    public ResponseEntity<UpdatedProjectDto> updateProject(@RequestBody UpdateProjectDto dto) {
        UpdatedProjectDto response = this.projectService.updateProject(dto);

        return ResponseEntity.ok(response);
    }
}
