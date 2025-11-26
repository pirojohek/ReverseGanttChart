package by.pirog.ReverseGanttChart.controller;

import by.pirog.ReverseGanttChart.dto.projectDto.CreateProjectDto;
import by.pirog.ReverseGanttChart.dto.projectDto.CreatedProjectDto;
import by.pirog.ReverseGanttChart.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/create")
    public ResponseEntity<CreatedProjectDto> createProject(@RequestBody CreateProjectDto createProjectDto) {

        return ResponseEntity.ok(projectService.createProject(createProjectDto));
    }
}
