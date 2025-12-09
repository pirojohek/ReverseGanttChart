package by.pirog.ReverseGanttChart.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserIsNotMemberInProjectException extends RuntimeException {
    public UserIsNotMemberInProjectException(String message) {
        super(message);
    }
}
