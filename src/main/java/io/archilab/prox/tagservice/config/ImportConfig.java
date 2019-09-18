package io.archilab.prox.tagservice.config;

import io.archilab.prox.tagservice.tag.recommendation.TagCounterUpdater;
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

  private boolean initialStart = true;

  @Bean
  public Executor taskExecutor() {
    return Executors.newScheduledThreadPool(100);
  }

  @Autowired
  private TagCounterUpdater tagCounterUpdater;


  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.setScheduler(taskExecutor());

    taskRegistrar.addTriggerTask(() -> tagCounterUpdater.updateTagCounter(), triggerContext -> {

      Calendar nextExecutionTime = new GregorianCalendar();

      if (initialStart) {
        initialStart = false;
        nextExecutionTime.add(Calendar.SECOND,
            Integer.valueOf(env.getProperty("tagCounter.delay.initial.seconds")));
        return nextExecutionTime.getTime();
      }

      nextExecutionTime.add(Calendar.MINUTE,
          Integer.valueOf(env.getProperty("tagCounter.delay.minutes")));

      return nextExecutionTime.getTime();
    });
  }
}
