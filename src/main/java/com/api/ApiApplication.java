package com.api;

import com.api.controller.Controller;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiApplication {

	private Controller controller;
	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}


}
