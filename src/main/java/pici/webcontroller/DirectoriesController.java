package pici.webcontroller;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;
import pici.database.DirectoryRepo;
import pici.database.dpos.DirectoryDPO;

@Controller
@Slf4j
public class DirectoriesController {

	
	@Autowired
	private DirectoryRepo dirRepo;
	
	@Value("${pica.main.gallerie}")
	private String mainGallerie;
	
	
	@GetMapping(value = {"/directories"})
	public String fillIndexModel(Model model) { 
		Iterable<DirectoryDPO> allDirs = dirRepo.findAll(Sort.by(Sort.Direction.ASC, "path"));
		HashMap<String, DirectoryDPO> dirMap = new HashMap<String, DirectoryDPO>();
		Set<String> rootDirs = new HashSet<String>();
		
		allDirs.forEach(d -> {
			String parentPath = null;
			
			if(d.getPath().getParent() == null) {
				parentPath = d.getPath().toString();
				rootDirs.add(parentPath);
			} else {
				parentPath =  getParent(d.getPath()).toString();
				rootDirs.add(parentPath);
			}
			
			if(!dirMap.containsKey(parentPath)) {
				dirMap.put(parentPath, d);
			} else {
				DirectoryDPO directoryDPO = dirMap.get(parentPath);
				directoryDPO.getSubDirs().add(d);
			}
			
		});
		
		log.info("Compiled Root Dirs: " + dirMap.size());
		model.addAttribute("dirList", rootDirs);
		model.addAttribute("dirTree", dirMap);
		
		return "views/directories";
	}


	private Path getParent(Path d) {
		if(d.getParent() != null) {
			return getParent(d.getParent());
		} else {
			return d;
		}
		
	}
	
}
