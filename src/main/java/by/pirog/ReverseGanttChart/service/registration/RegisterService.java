package by.pirog.ReverseGanttChart.service.registration;

import by.pirog.ReverseGanttChart.dto.RegisteredResponseDto;
import by.pirog.ReverseGanttChart.dto.RegistrationDto;

public interface RegisterService {

    RegisteredResponseDto register(RegistrationDto registrationDto);
}
