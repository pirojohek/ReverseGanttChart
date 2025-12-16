package by.pirog.ReverseGanttChart.dto;
// Todo сделать валидацию регистрации

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record RegistrationDto(

        @NotNull
        String username,

        @Email
        String email,

        @NotNull
        String password
){

}
