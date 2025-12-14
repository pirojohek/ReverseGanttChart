package by.pirog.ReverseGanttChart.dto.membershipDto;

import lombok.Builder;

@Builder
public record UpdateProfileRequestDto(
        String username
) {
}
