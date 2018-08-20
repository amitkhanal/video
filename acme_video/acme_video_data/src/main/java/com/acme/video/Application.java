package com.acme.video;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author amitkhanal
 * 
 * Main class that registers as {@link SpringBootApplication}
 */
@SpringBootApplication(scanBasePackages = { "com.acme.video" })
public class Application {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}
}
