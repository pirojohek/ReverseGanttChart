package by.pirog.ReverseGanttChart.controller;


import by.pirog.ReverseGanttChart.dto.studentStatusDto.SetTaskStatusRequestDto;
import by.pirog.ReverseGanttChart.dto.studentStatusDto.StudentTaskStatusResponseDto;
import by.pirog.ReverseGanttChart.service.studentTaskStatus.StudentTaskStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studentTaskStatus")
public class StudentTaskStatusController {

    private final StudentTaskStatusService taskStatusService;

    @PostMapping("/setTaskStatus")
    public ResponseEntity<StudentTaskStatusResponseDto> setTaskStatus(@RequestBody SetTaskStatusRequestDto dto) {
        StudentTaskStatusResponseDto response = this.taskStatusService.setTaskStatus(dto);
        return ResponseEntity.ok(response);
    }

}
