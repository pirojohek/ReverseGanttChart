package by.pirog.ReverseGanttChart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReviewerTaskStatusNotFound extends RuntimeException {
    public ReviewerTaskStatusNotFound(String message) {
        super(message);
    }
}
