package pici.batch;

import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ScanDirectoryJobLauncher {


	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	@Qualifier("scanDirectoriesJob")
	Job job;

	public void scanDirectory(String rootDirectory) throws Exception {
		JobParametersBuilder jobBuilder= new JobParametersBuilder();

		jobBuilder.addString("rootDirectory", rootDirectory);
		jobBuilder.addDate("currentDate", new Date());

		jobLauncher.run(job, jobBuilder.toJobParameters());
	}

}

