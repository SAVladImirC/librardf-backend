package mk.ukim.finki.librardf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class LibrardfApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibrardfApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins("*")  // Allow all origins
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // Allow specific methods
						.allowedHeaders("*");  // Allow all headers
			}
		};
	}
}
