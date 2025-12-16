package by.pirog.ReverseGanttChart.dto.invite;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
public record InviteRequestDto(

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,

    @NotNull(message = "Project Id is required")
    Long projectId,

    @NotNull(message = "Role is Required")
    String role){
    
}
