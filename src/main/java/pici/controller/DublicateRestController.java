package pici.controller;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import pici.batch.ScanDirectoryJobLauncher;
import pici.database.FileRepo;
import pici.database.dpos.FileDPO;

@RestController
@Slf4j
public class DublicateRestController {

	@Autowired
	private FileRepo fileRepo;

	@Autowired
	private ScanDirectoryJobLauncher jobLauncher;
	
	@GetMapping(value = {"/scanDir"})
	public void scanDirectory (@RequestParam("dirName") String dirName) throws IOException {
		try {
			jobLauncher.scanDirectory(dirName);
		} catch (Exception e) {
			log.error("Error during start of directory scan", e);
		}
	}
	
	@GetMapping(value = {"/duplicates"})
	public Page<FileDPO> fillIndexModel( 
			  @RequestParam("start") Optional<Integer> start, 
		      @RequestParam("length") Optional<Integer> size) {

        int pageSize = size.orElse(10);
        int currentPage = start.orElse(0) / pageSize;
        
        log.info("Currentpage={}, PageSize={}", currentPage, pageSize);


        Page<FileDPO> listDuplicates = fileRepo.listDuplicates(PageRequest.of(currentPage, pageSize));
		
		return listDuplicates;
	}

}
