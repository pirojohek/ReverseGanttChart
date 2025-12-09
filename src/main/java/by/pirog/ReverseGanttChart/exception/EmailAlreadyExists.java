package by.pirog.ReverseGanttChart.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // 409 - Конфликт (уже существует)
public class EmailAlreadyExists extends RuntimeException {
    public EmailAlreadyExists(String message) {
        super(message);
    }
}
