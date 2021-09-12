package pici.batch;

import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class ScanDirectoryJobLauncher {

	@Autowired
	@Qualifier("asyncJobLauncher")
	private JobLauncher jobLauncher;

	@Autowired
	@Qualifier("scanDirectoriesJob")
	private Job job;

	public void scanDirectory(String rootDirectory) throws Exception {
		JobParametersBuilder jobBuilder= new JobParametersBuilder();

		jobBuilder.addString("rootDirectory", rootDirectory);
		jobBuilder.addDate("currentDate", new Date());

		jobLauncher.run(job, jobBuilder.toJobParameters());
	}
	
	@Bean(name = "asyncJobLauncher")
	public JobLauncher simpleJobLauncher(JobRepository jobRepository) throws Exception {
	    SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
	    jobLauncher.setJobRepository(jobRepository);
	    jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
	    jobLauncher.afterPropertiesSet();
	    return jobLauncher;
	}

}

