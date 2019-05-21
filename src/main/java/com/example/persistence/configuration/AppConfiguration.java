package com.example.persistence.configuration;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Top/Root level app configuration class
 * 
 * @author amit
 *
 */

@Configuration
@ComponentScan(basePackages = "com.example.persistence")
@Import(value = { AppPersistenceConfiguration.class })
@EnableAsync // this actually enables Async method execution, adding @Async is not sufficient.
public class AppConfiguration implements AsyncConfigurer {
	// Nothing to do here for now.

	/**
	 * Declares AsyncTaskExecutor to be use in entire application to abstract Thread
	 * pool and async execution.
	 * 
	 * @return
	 */
	@Bean
	public AsyncTaskExecutor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5); // minimum threads count to be maintained
		executor.setQueueCapacity(25);
		executor.setThreadNamePrefix("custom"); // prefix attached to monitor custom threads in JProfiler
		executor.setWaitForTasksToCompleteOnShutdown(true); // Forces spring to wait for all Tasks submitted to complete
		// before shutdown of application.
		executor.setAwaitTerminationSeconds(30); // Time spring should wait to shutdown application container and
		// resources if tasks are running at application shutdown time.
		return executor;
	}
	
	/*
	 * By default, spring uses SimpleAsyncTaskExecutor (which do not uses thread pool and creates new thread at each invocation).
	 * To override that default TaskExecutor, we can override this method and specify TaskExecutor to be used.
	 * 
	 * This is how we can declare Executor to be used for all @Async uses in entire application.
	 * If we provide executor here, we do not need to specify executor at @Async annotation usage.
	 */
	@Override
	public Executor getAsyncExecutor() {
		// ThreadPoolTaskExecutor uses configured thread pool and do not spawn at each invocation.
		return threadPoolTaskExecutor();
	}
	
	
}
