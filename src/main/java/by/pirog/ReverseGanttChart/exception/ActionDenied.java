package by.pirog.ReverseGanttChart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN) // 403 - Доступ запрещен
public class ActionDenied extends RuntimeException {
    public ActionDenied(String message) {
        super(message);
    }
}
