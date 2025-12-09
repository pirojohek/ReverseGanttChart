package by.pirog.ReverseGanttChart.service.reviewerTaskStatus;

import by.pirog.ReverseGanttChart.dto.reviwerStatusDto.ReviewerTaskStatusResponseDto;
import by.pirog.ReverseGanttChart.dto.reviwerStatusDto.SetReviewerTaskStatusRequestDto;

public interface ReviewerTaskStatusService {

    ReviewerTaskStatusResponseDto setReviewerTaskStatus(SetReviewerTaskStatusRequestDto setReviewerTaskStatusRequestDto);
}
