package com.homeProject.JennsArtWebsite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class JennsArtWebsiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(JennsArtWebsiteApplication.class, args);
	}

}
