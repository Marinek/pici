package pici.webcontroller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;
import pici.batch.ScanDirectoryJobLauncher;
import pici.database.FileRepo;
import pici.database.dpos.DirectoryDPO;
import pici.database.dpos.FileDPO;

@Controller
@Slf4j
public class IndexController {
	
	@Autowired
	private ScanDirectoryJobLauncher jobLauncher;
	
	@Autowired
	private FileRepo fileRepo;

	@GetMapping(value = {"/", "index.html"})
	public String fillIndexModel(ModelAndView model) { 
		
		model.addObject("directory", new DirectoryDPO());
		
		return "views/index";
	}
	
	@PostMapping(value = {"/", "index.html"})
	public String startSearch(@ModelAttribute DirectoryDPO directory, BindingResult errors, Model model) {
		
		try {
			jobLauncher.scanDirectory(directory.getPathName());
		} catch (Exception e) {
			log.error("Error during start of directory scan", e);
		}
		
		return "views/index";
	}

	@ResponseBody
	@GetMapping("/image/{id}")
	public void getImageAsResponseEntity(HttpServletResponse response, @PathVariable("id") Long id) throws IOException {
	    
		Optional<FileDPO> findById = fileRepo.findById(id);
		
		response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
	    
	    response.getOutputStream().write(Files.newInputStream(Path.of(findById.get().getDirectory().getPath().toString(), findById.get().getFileName())).readAllBytes());
		response.getOutputStream().close();
	}

}