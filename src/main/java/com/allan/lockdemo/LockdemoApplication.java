package com.allan.lockdemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.allan.lockdemo.mapper"})
public class LockdemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(LockdemoApplication.class, args);
	}
}
