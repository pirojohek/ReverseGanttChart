package by.pirog.ReverseGanttChart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class DefaultServerException extends RuntimeException {
    public DefaultServerException(String message) {
        super(message);
    }
}
