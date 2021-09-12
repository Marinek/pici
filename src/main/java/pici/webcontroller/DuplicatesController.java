package pici.webcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import pici.database.FileRepo;
import pici.database.dpos.FileDPO;

@Controller
public class DuplicatesController {

	@Autowired
	private FileRepo fileRepo;

	@GetMapping(value = {"/dubletten"})
	public String fillIndexModel(ModelAndView model) { 


		return "views/dubletten";
	}

	@GetMapping(value = {"vDuplicates"})
	public String viewDuplicates(Model model) { 
		Page<FileDPO> listDuplicates = fileRepo.listDuplicates(PageRequest.of(1, 11));
		
		model.addAttribute("sumDups", listDuplicates.getTotalElements());
		
		model.addAttribute("duplicates", listDuplicates.getContent());
		
		return "views/duplicates";
	}

	@GetMapping(value = {"vDuplicates/{fileId}"})
	public String viewDuplicatesDetails(@PathVariable Long fileId, Model model) { 
		FileDPO fileDPO = fileRepo.findById(fileId).get();
		model.addAttribute("dup", fileDPO);
		model.addAttribute("dupList", fileRepo.findByContentHash(fileDPO.getContentHash()));
		
		return "views/duplicates::detail";
	}
	
	
}