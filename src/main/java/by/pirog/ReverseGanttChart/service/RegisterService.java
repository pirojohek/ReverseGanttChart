package by.pirog.ReverseGanttChart.service;

import by.pirog.ReverseGanttChart.dto.RegisteredResponseDto;
import by.pirog.ReverseGanttChart.dto.RegistrationDto;

public interface RegisterService {

    RegisteredResponseDto register(RegistrationDto registrationDto);
}
