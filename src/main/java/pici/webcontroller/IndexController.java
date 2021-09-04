package pici.webcontroller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import pici.database.FileRepo;
import pici.database.dpos.FileDPO;

@Controller
public class IndexController {
	
	@Autowired
	private FileRepo fileRepo;

	@GetMapping(value = {"/", "index.html"})
	public String fillIndexModel(Model model) { 
		
		return "index";
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