package co.com.crediya.config;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import co.com.crediya.model.application.gateways.ApplicationRepository;
import co.com.crediya.model.application.gateways.UserGateway;
import co.com.crediya.model.loanstatus.gateways.LoanStatusRepository;
import co.com.crediya.model.loantype.gateways.LoanTypeRepository;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            String[] beanNames = context.getBeanDefinitionNames();

            boolean useCaseBeanFound = false;
            for (String beanName : beanNames) {
                if (beanName.endsWith("UseCase")) {
                    useCaseBeanFound = true;
                    break;
                }
            }

            assertTrue(useCaseBeanFound, "No beans ending with 'Use Case' were found");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {

        @Bean
        public MyUseCase myUseCase() {
            return new MyUseCase();
        }

        @Bean
        public ApplicationRepository applicationRepository() {
            return Mockito.mock(ApplicationRepository.class);
        }

        @Bean
        public LoanTypeRepository loanTypeRepository() {
            return Mockito.mock(LoanTypeRepository.class);
        }

        @Bean
        public LoanStatusRepository loanStatusRepository() {
            return Mockito.mock(LoanStatusRepository.class);
        }

        @Bean
        public UserGateway userGateway() {
            return Mockito.mock(UserGateway.class);
        }
    }

    static class MyUseCase {
        public String execute() {
            return "MyUseCase Test";
        }
    }
}