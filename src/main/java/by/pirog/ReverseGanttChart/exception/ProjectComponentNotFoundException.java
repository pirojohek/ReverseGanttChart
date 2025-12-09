package by.pirog.ReverseGanttChart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // 404 - Не найдено
public class ProjectComponentNotFoundException extends RuntimeException {
    public ProjectComponentNotFoundException(String message) {
        super(message);
    }
}
