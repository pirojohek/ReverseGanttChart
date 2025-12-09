package by.pirog.ReverseGanttChart.controller;


import by.pirog.ReverseGanttChart.dto.reviwerStatusDto.ReviewerTaskStatusResponseDto;
import by.pirog.ReverseGanttChart.dto.reviwerStatusDto.SetReviewerTaskStatusRequestDto;
import by.pirog.ReverseGanttChart.service.reviewerTaskStatus.ReviewerTaskStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviewerTaskStatus")
public class ReviewerTaskStatusController {

    private final ReviewerTaskStatusService reviewerTaskStatusService;


    @PostMapping("/setTaskStatus")
    public ResponseEntity<ReviewerTaskStatusResponseDto> setTaskStatus(@RequestBody SetReviewerTaskStatusRequestDto dto){
        ReviewerTaskStatusResponseDto response = this.reviewerTaskStatusService.setReviewerTaskStatus(dto);
        return ResponseEntity.ok(response);
    }
}
