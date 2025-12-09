package by.pirog.ReverseGanttChart.controller;

import by.pirog.ReverseGanttChart.dto.taskMakerDto.TakenTaskResponseDto;
import by.pirog.ReverseGanttChart.service.taskMakers.TaskMakersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    // Todo- нужно добавить обработку ошибок, что пользователь не может взять задачу повторно если он там есть

    @PostMapping("/setMaker")
    public ResponseEntity<List<TakenTaskResponseDto>> giveMembershipTasksToMake(@RequestParam("email") String email,
                                                                                @RequestParam("taskId") Long taskId,
                                                                                @RequestParam("subtasks") Boolean subtasks){
        List<TakenTaskResponseDto> response = this.taskMakersService.giveMembershipTasksToMake(email, taskId, subtasks);

        return ResponseEntity.ok(response);
    }

    // Todo нужно еще добавить изменение исполняющего задачу, то есть его удалить ну тут уже без рекурсии

    @DeleteMapping
    public ResponseEntity<?> removeMembershipTasksMaker(@RequestParam("taskMakerId") Long taskId) {
        this.taskMakersService.removeTaskMakerById(taskId);
        return ResponseEntity.noContent().build();
    }
}
