package io.archilab.prox.tagservice.config;

import io.archilab.prox.tagservice.tags.TagCounterUpdater;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class ImportConfig implements SchedulingConfigurer {

  @Autowired
  private Environment env;


  @Bean
  public Executor taskExecutor() {
    return Executors.newScheduledThreadPool(100);
  }

  @Autowired
  private TagCounterUpdater tagCounterUpdater;


  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.setScheduler(taskExecutor());

    // TagCounterService
    taskRegistrar.addTriggerTask(() -> tagCounterUpdater.updateTagCounter(), triggerContext -> {

      Calendar nextExecutionTime = new GregorianCalendar();

      nextExecutionTime.add(Calendar.SECOND,
          Integer.valueOf(env.getProperty("tagRecommendationCalculation.delay.seconds")));
      return nextExecutionTime.getTime();
    });
  }
}
