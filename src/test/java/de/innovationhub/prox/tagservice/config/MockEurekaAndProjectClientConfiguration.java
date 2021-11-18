package de.innovationhub.prox.tagservice.config;


import com.netflix.discovery.EurekaClient;
import de.innovationhub.prox.tagservice.net.ProjectClient;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockEurekaAndProjectClientConfiguration {

  // TODO get rid of mocking the beans and find a clean solution

  @Bean
  @Qualifier("eurekaClient")
  @ConditionalOnMissingBean
  public EurekaClient eurekaClient() {
    return Mockito.mock(EurekaClient.class);
  }

  @MockBean @InjectMocks public ProjectClient projectClient;
}
