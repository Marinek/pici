package pici.batch;

import org.springframework.batch.core.JobInterruptedException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ImportJob {

	
	@Bean
	public Step addDirs() {
		return new Step() {
			
			@Override
			public boolean isAllowStartIfComplete() {
				return false;
			}
			
			@Override
			public int getStartLimit() {
				return 1;
			}
			
			@Override
			public String getName() {
				return "Add directories to scan";
			}
			
			@Override
			public void execute(StepExecution stepExecution) throws JobInterruptedException {
				// TODO Auto-generated method stub
				
			}
		};
	}
}
