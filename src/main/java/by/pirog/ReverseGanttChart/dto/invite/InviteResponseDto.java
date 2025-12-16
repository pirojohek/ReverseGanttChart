package by.pirog.ReverseGanttChart.dto.invite;


import by.pirog.ReverseGanttChart.enums.InviteStatus;
import by.pirog.ReverseGanttChart.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InviteResponseDto {

    private InviteStatus inviteStatus;

    private String userRole;

    private String invitedPersonEmail;

    private String inviter;

    @JsonFormat(pattern = "yyyy-MM-dd HH-mm:ss")
    private LocalDateTime inviteDate;
}
