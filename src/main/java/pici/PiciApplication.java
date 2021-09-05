package pici;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.slf4j.Slf4j;
import pici.webcontroller.ThymeleafLayoutInterceptor;

@SpringBootApplication
@EnableBatchProcessing
@Slf4j
public class PiciApplication implements WebMvcConfigurer {
	
	@Autowired
	private ThymeleafLayoutInterceptor interceptor;
	
	public static void main(String[] args) {
		SpringApplication.run(PiciApplication.class, args);
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		log.info("Add interceptors...");
		
		WebMvcConfigurer.super.addInterceptors(registry);
		
		registry.addInterceptor(interceptor);
	}
}
