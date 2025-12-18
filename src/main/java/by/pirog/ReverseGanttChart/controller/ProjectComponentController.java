package by.pirog.ReverseGanttChart.controller;

import by.pirog.ReverseGanttChart.dto.projectComponentDto.*;
import by.pirog.ReverseGanttChart.service.projectComponent.ProjectComponentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projectComponent")
public class ProjectComponentController {
    private final ProjectComponentService projectComponentService;

    @PostMapping("/action/create")
    public ResponseEntity<CreatedProjectComponentDto> createProjectComponent(@RequestBody CreateProjectComponentDto createProjectComponentDto) {
        CreatedProjectComponentDto response = this.projectComponentService.createProjectComponent(createProjectComponentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ProjectComponentResponseDto> getProjectComponentByIdWithoutHierarchy(@RequestParam(name = "componentId") Long componentId){
        ProjectComponentResponseDto response = this.projectComponentService.getProjectComponentByIdWithoutHierarchy(componentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProjectComponentResponseDto>> getAllProjectComponents(){
        return ResponseEntity.ok(this.projectComponentService.getProjectComponentsByProjectIdWithHierarchyOrderedByDate());
    }

    @PatchMapping("/action/update")
    public ResponseEntity<UpdatedProjectComponentResponseDto>  updateProjectComponent(@RequestBody UpdateProjectComponentRequestDto updateProjectComponentRequestDto){
        UpdatedProjectComponentResponseDto response = this.projectComponentService.patchProjectComponent(updateProjectComponentRequestDto);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/action/delete")
    public ResponseEntity<Void> deleteProjectComponent(@RequestParam(name = "componentId") Long componentId){
        this.projectComponentService.deleteProjectComponentById(componentId);
        return ResponseEntity.noContent().build();
    }
}
