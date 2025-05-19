package com.arslan.zzz;

import org.springframework.boot.SpringApplication;

public class TestZzzApplication {

	public static void main(String[] args) {
		SpringApplication.from(Application::main).with(TestcontainersConfiguration.class).run(args);
	}

}
