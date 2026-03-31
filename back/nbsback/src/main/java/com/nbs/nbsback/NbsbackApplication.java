package com.nbs.nbsback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
    info = @Info(
        title = "NBS Back API",
        version = "1.0",
        description = "API documentation for NBS Back"
    )
)
@SpringBootApplication
public class NbsbackApplication {

	public static void main(String[] args) {
		SpringApplication.run(NbsbackApplication.class, args);
	}

}
