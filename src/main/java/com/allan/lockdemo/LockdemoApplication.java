package com.allan.lockdemo;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.allan.lockdemo.mapper"})
@EnableEncryptableProperties //启用数据库密码加密功能
public class LockdemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(LockdemoApplication.class, args);
	}
}
