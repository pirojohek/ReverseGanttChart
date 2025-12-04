package by.pirog.ReverseGanttChart.controller;

import by.pirog.ReverseGanttChart.dto.projectComponentDto.CreateProjectComponentDto;
import by.pirog.ReverseGanttChart.dto.projectComponentDto.CreatedProjectComponentDto;
import by.pirog.ReverseGanttChart.dto.projectComponentDto.ProjectComponentResponseDto;
import by.pirog.ReverseGanttChart.service.projectComponent.ProjectComponentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projectComponent")
public class ProjectComponentController {
    private final ProjectComponentService projectComponentService;


    /*
    при получении всех задач у проекта нужно определить задачи, которые являются корнем дерева, то есть
    они не имеют родителей, эти задачи сортируются в порядке добавления по времени или по порядку order
    если такой имеется, для начала сделаю по времени
    затем он должен сформировать response, который видимо рекурсивно получается
    начнем с создания компонентов, а уже потом с их вывода
     */

    @PostMapping("/action/create")
    public ResponseEntity<CreatedProjectComponentDto> createProjectComponent(@RequestBody CreateProjectComponentDto createProjectComponentDto) {
        CreatedProjectComponentDto response = this.projectComponentService.createProjectComponent(createProjectComponentDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ProjectComponentResponseDto> getProjectComponentById(@RequestParam(name = "componentId") Long componentId){
        ProjectComponentResponseDto response = this.projectComponentService.getProjectComponentById(componentId);
        return ResponseEntity.ok(response);
    }
//
//    @GetMapping("/all")
//    public ResponseEntity<List<ProjectComponentResponseDto>> getAllProjectComponents(){
//
//    }

}
