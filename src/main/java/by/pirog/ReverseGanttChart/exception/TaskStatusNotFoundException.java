package by.pirog.ReverseGanttChart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TaskStatusNotFoundException extends RuntimeException {
    public TaskStatusNotFoundException(String message) {
        super(message);
    }
}
