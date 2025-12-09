package by.pirog.ReverseGanttChart.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserIsNotTaskMakerException extends RuntimeException {
    public UserIsNotTaskMakerException(String message) {
        super(message);
    }
}
