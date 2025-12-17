package by.pirog.ReverseGanttChart.controller;

import by.pirog.ReverseGanttChart.controller.InviteController;
import by.pirog.ReverseGanttChart.dto.invite.InviteRequestDto;
import by.pirog.ReverseGanttChart.service.invite.ProjectInviteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InviteController.class)
@AutoConfigureMockMvc(addFilters = false)
public class InviteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectInviteService projectInviteService;

    private static final String SEND_URL = "/api/invite/send";
    private static final String ACCEPT_URL = "/api/invite/accept";

    @Test
    void shouldSendInvite_whenRequestIsValid() throws Exception {
        String json = """
                {
                    "email": "test@mail.com",
                    "projectId": 1,
                    "role": "USER"
                }
                """;
        mockMvc.perform(post(SEND_URL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        verify(projectInviteService, times(1))
                .sendInvitation(any(InviteRequestDto.class));

    }

    @Test
    void shouldReturn400AndNotCallsService_whenRequestIsInvalid() throws Exception {
        String json = """
                {
                    "email": "test",
                    "projectId": 1,
                    "role": "USER"
                }
                """;
        mockMvc.perform(post(SEND_URL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(projectInviteService, never()).sendInvitation(any(InviteRequestDto.class));
    }

    @Test
    void shouldReturn400AndNotCallsService_whenFieldsIsMissing() throws Exception {
        String json = """
                {
                    "email": "test@mail.ru",
                    "role": "USER"
                }
                """;
        mockMvc.perform(post(SEND_URL)
        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(projectInviteService, never()).sendInvitation(any(InviteRequestDto.class));
    }

    @Test
    void shouldAcceptInvite_whenRequestIsValid() throws Exception {
        String token = "valid_token";
        String username = "test_user";

        mockMvc.perform(post(ACCEPT_URL)
                .param("token", token)).andExpect(status().isOk());

        verify(projectInviteService, times(1)).acceptInvitation(token);
    }
}
