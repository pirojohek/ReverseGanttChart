package by.pirog.ReverseGanttChart.controller;

import by.pirog.ReverseGanttChart.dto.taskMakerDto.TakenTaskResponseDto;
import by.pirog.ReverseGanttChart.service.taskMakers.TaskMakersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/taskMakers")
public class TaskMakersController {

    private final TaskMakersService taskMakersService;

    // Здесь будет параметр, если true, взять задачу и все подзадачи к ней
    @PostMapping("/take")
    public ResponseEntity<List<TakenTaskResponseDto>> takeTaskToMake(@RequestParam("taskId") Long taskId, @RequestParam("subtasks") Boolean subtasks){

        List<TakenTaskResponseDto> response = this.taskMakersService.takeTasksToMake(taskId, subtasks);

        return ResponseEntity.ok(response);
    }
}
