package com.marathon.board.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.marathon.board.config.SecurityConfig;
import org.eclipse.jdt.internal.compiler.batch.Main;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(MainController.class)
public class MainControllerTest {

    private final MockMvc mvc;

    public MainControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    void givenNoting_WhenRequestingRootPage_thenRedirectsToArticlesPage() throws Exception {
        //given

        //when
        mvc.perform(get("/"))
            .andExpect(status().is3xxRedirection());

        //then
    }

}
