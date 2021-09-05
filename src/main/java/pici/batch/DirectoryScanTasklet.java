package pici.batch;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import pici.database.DirectoryRepo;
import pici.database.dpos.DirectoryDPO;

@Component
@Slf4j
public class DirectoryScanTasklet implements Tasklet {

	@Autowired
	private DirectoryRepo dirRepo;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		log.info("execute" + chunkContext);
		String rootPathToScan = chunkContext.getStepContext().getStepExecution().getJobParameters().getString("rootDirectory");

		log.info("Start walking @" + rootPathToScan);
		Path rootPath = Path.of(rootPathToScan);

		final Map<Path, DirectoryDPO> explodePath = new HashMap<Path, DirectoryDPO>();

		Files.walkFileTree(rootPath, new FileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				log.info("preVisitDirectory: " + dir.toString());

				DirectoryDPO directoryDPO = dirRepo.findOneByPath(dir).orElse(new DirectoryDPO());
				directoryDPO.setScheduledScan(true);
				directoryDPO.setPath(dir);
				directoryDPO.setCount(0);

				directoryDPO = dirRepo.save(directoryDPO);

				explodePath.put(dir,  directoryDPO);

				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				log.info("Visiting file: " + file);

				DirectoryDPO directoryDPO = explodePath.get(file.getParent());

				directoryDPO.incrementCount();

				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				log.error("Visiting file failed" + file, exc);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				log.info("PostVisitDirectory: " + dir.toString());

				boolean finishedSearch = Files.isSameFile(dir, rootPath);
				if (finishedSearch) {
					System.out.println("-------------------------------");
					return FileVisitResult.TERMINATE;
				}

				return FileVisitResult.CONTINUE;
			}

		});

		return RepeatStatus.FINISHED;
	}

}