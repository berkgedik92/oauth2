package project.diagram;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import project.diagram.security.config.OAuthProperties;

@SpringBootApplication
@Configuration
@EnableScheduling
@EnableConfigurationProperties(OAuthProperties.class)
public class ServerApplication extends SpringBootServletInitializer implements CommandLineRunner, WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

	@Override
	public void run(String... args) {

    }
}

