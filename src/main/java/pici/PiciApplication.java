package pici;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PiciApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(PiciApplication.class, args);
	}
	
//	@Bean
//	public CommandLineRunner scannerGO(PathScanner scanner) {
//		return args -> {
//
//			try {
//				Map<Path, DirectoryDPO> scan = scanner.scan(Paths.get("G:", "OneDrive - Familie Gasse", "Bilder"));
//				
//				System.err.println("ENDE");
//				
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//		};
//		
//	}
	
}
