package com.himedia.project_a_team04_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class ProjectATeam04BackendApplication {

	public static void main(String[] args) {
		System.out.println("서버 자동재시작 테스트...");

		SpringApplication.run(ProjectATeam04BackendApplication.class, args);
	}
}
