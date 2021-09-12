package pici.webcontroller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import pici.database.FileRepo;
import pici.database.dpos.FileDPO;

@Controller
public class DuplicatesController {

	@Autowired
	private FileRepo fileRepo;
	
	@Value("${pica.main.gallerie}")
	private String mainGallerie;

	@GetMapping(value = {"/dubletten"})
	public String fillIndexModel(ModelAndView model) { 


		return "views/dubletten";
	}

	@GetMapping(value = {"vDuplicates"})
	public String viewDuplicates( @RequestParam(value="type", required=false) Optional<String> type , Model model) { 
		String showType = type.orElse("duplicates");
		
		Page<FileDPO> listDuplicates = fileRepo.listDuplicates(PageRequest.of(1, 11));
		Page<FileDPO> findOutOfMainDirectory = fileRepo.findOutOfMainDirectory(mainGallerie + "%", PageRequest.of(1, 11));
		
		model.addAttribute("sumDups", listDuplicates.getTotalElements());
		model.addAttribute("sumExt", findOutOfMainDirectory.getTotalElements());
		
		model.addAttribute("mainPath", mainGallerie);
		
		switch (showType) {
			case "duplicates" : model.addAttribute("duplicates", listDuplicates.getContent()); break;
			case "externals" :  model.addAttribute("duplicates", findOutOfMainDirectory.getContent()); break;
		}
		
		return "views/duplicates";
	}

	@GetMapping(value = {"vDuplicates/{fileId}"})
	public String viewDuplicatesDetails(@PathVariable Long fileId, Model model) { 
		FileDPO fileDPO = fileRepo.findById(fileId).get();
		
		model.addAttribute("mainPath", mainGallerie);
		model.addAttribute("dup", fileDPO);
		model.addAttribute("dupList", fileRepo.findByContentHash(fileDPO.getContentHash()));
		
		return "views/duplicates::detail";
	}
	
	
}