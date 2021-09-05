package pici.webcontroller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class DublettenController {

	@GetMapping(value = {"/dubletten"})
	public String fillIndexModel(ModelAndView model) { 
		
		
		return "views/dubletten";
	}
	
}
