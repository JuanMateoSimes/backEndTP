package com.utn.frc.backend.pruebaservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PruebaServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PruebaServiceApplication.class, args);
	}

}
