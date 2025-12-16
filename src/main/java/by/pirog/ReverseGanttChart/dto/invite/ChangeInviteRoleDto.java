package by.pirog.ReverseGanttChart.dto.invite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeInviteRoleDto {

    String email;

    String role;
}
