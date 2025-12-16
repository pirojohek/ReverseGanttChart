package by.pirog.ReverseGanttChart.exception;
// todo - добавить статус ошибки
public class CannotSendInviteException extends RuntimeException {
    public CannotSendInviteException(String message) {
        super(message);
    }
}
