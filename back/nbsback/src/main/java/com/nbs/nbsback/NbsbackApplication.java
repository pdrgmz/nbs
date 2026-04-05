package com.nbs.nbsback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.nbs.nbsback.clients")
@SpringBootApplication
public class NbsbackApplication {

	public static void main(String[] args) {
		SpringApplication.run(NbsbackApplication.class, args);
	}

}
