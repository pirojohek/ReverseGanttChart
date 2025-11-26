package by.pirog.ReverseGanttChart.exception;

public class UserIsNotMemberInProjectException extends RuntimeException {
    public UserIsNotMemberInProjectException(String message) {
        super(message);
    }
}
