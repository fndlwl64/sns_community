package com.springboot.sns_community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.web.bind.support.DefaultSessionAttributeStore;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class SnsCommunityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SnsCommunityApplication.class, args);
	}

}
