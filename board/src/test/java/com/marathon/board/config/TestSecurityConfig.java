package com.marathon.board.config;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import com.marathon.board.domain.UserAccount;
import com.marathon.board.repository.UserAccountRepository;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

@Import(SecurityConfig.class)
public class TestSecurityConfig {


    @MockBean
    private UserAccountRepository userAccountRepository;

    @BeforeTestMethod
    public void securitySetUp() {
        given(userAccountRepository.findById(anyString())).willReturn(
            Optional.of(
               UserAccount.of(
                   "unoTest",
                   "pw",
                   "uno-test@gmail.com",
                   "uno-test",
                   "test-memo"
               )
            )
        );
    }


}
