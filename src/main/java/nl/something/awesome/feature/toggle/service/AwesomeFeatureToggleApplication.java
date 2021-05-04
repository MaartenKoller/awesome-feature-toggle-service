package nl.something.awesome.feature.toggle.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AwesomeFeatureToggleApplication {

	public static void main(String[] args) {
		SpringApplication.run(AwesomeFeatureToggleApplication.class, args);
	}
}
