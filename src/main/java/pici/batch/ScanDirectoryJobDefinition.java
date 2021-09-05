package pici.batch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Sort;

import pici.database.DirectoryRepo;
import pici.database.dpos.DirectoryDPO;

@Configuration
public class ScanDirectoryJobDefinition {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job scanDirectoriesJob (JobRepository jobRepository,
			@Qualifier("collectDirectories") Step collectDirectories,
			@Qualifier("processtDirectories") Step processtDirectories
			) {
		return jobBuilderFactory.get("scanDirs")
				.repository(jobRepository)
				.start(collectDirectories)
				.next(processtDirectories)
				.build();
	}

	@Bean
	public Step collectDirectories(DirectoryScanTasklet tasklet) {
		return stepBuilderFactory.get("collectDirectories").
				tasklet(tasklet)
				.build();
	}
	
	@Bean
	public Step processtDirectories(JobRepository jobRepository,
			RepositoryItemReader<DirectoryDPO> itemReader,
			DirectoryProcessor processor,
			TaskExecutor taskExecutor,
			ItemWriter<DirectoryDPO> itemWriter
			) {
		
		return stepBuilderFactory.get("processtDirectories")
				.repository(jobRepository)
				.<DirectoryDPO, DirectoryDPO>chunk(10)
				.reader(itemReader)
				.processor(processor)
				.writer(itemWriter)
				.taskExecutor(taskExecutor)
				.build();
	}
	
	@Bean
	public RepositoryItemReader<DirectoryDPO> itemReader(DirectoryRepo repo) {
		Map<String, Sort.Direction> sorts = new HashMap<String, Sort.Direction>();
		
		sorts.put("id", Sort.Direction.ASC);
		
		return new RepositoryItemReaderBuilder<DirectoryDPO>()
			.name("dirsToScan")
			.methodName("listDirectoriesForScan")
			.pageSize(10)
			.repository(repo)
			.sorts(sorts)
			.build();
	}
	
	@Bean
	public ItemWriter<DirectoryDPO> itemWriter(final DirectoryRepo repo) {
		return new ItemWriter<DirectoryDPO>() {
			
			@Override
			public void write(List<? extends DirectoryDPO> items) throws Exception {
				for(DirectoryDPO item : items) {
					item.setScheduledScan(false);
					repo.save(item);
				}
			}
		};
	}
	
	@Bean
	public TaskExecutor taskExecutor() {
	    return new SimpleAsyncTaskExecutor("DIRECTORY SCAN");
	}

}
