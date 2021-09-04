package pici.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job scanDirectoriesJob(Step addDirs) {
		return jobBuilderFactory.get("scanDirs")
				.listener(new JobExecutionListener() {
					
					@Override
					public void beforeJob(JobExecution jobExecution) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void afterJob(JobExecution jobExecution) {
						// TODO Auto-generated method stub
						
					}
				})
				.flow(addDirs)
				.end()
				.build();
	}

}
