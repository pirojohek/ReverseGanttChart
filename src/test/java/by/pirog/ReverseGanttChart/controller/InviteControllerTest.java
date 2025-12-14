package by.pirog.ReverseGanttChart.controller;

import by.pirog.ReverseGanttChart.controller.InviteController;
import by.pirog.ReverseGanttChart.dto.invite.InviteRequestDto;
import by.pirog.ReverseGanttChart.service.invite.ProjectInviteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InviteController.class)
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
                "email": "test@mail.com",
                "projectId": 1,
                "role": "USER"
                """;
        mockMvc.perform(post(SEND_URL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        verify(projectInviteService)
                .sendInvitation(any(InviteRequestDto.class));

    }
}
