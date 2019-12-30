package com.example.myfiles;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.MultipartConfigElement;

@SpringBootApplication
public class MyfilesApplication extends WebMvcConfigurerAdapter{

	public static void main(String[] args) {
		SpringApplication.run(MyfilesApplication.class, args);
	}

	@Bean
	public MultipartConfigElement multipartConfigElement(){
		MultipartConfigFactory factory = new MultipartConfigFactory();
		factory.setMaxFileSize("300MB");
		return factory.createMultipartConfig();
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("myfiles");

		registry.addViewController("/myfiles").setViewName("myfiles");
		registry.addViewController("/admin_index").setViewName("admin");
		registry.addViewController("/admin_users").setViewName("admin_users");
		registry.addViewController("/login").setViewName("login");
		registry.addViewController("/reg").setViewName("reg");
		registry.addViewController("/forget").setViewName("forget");
//		registry.addViewController("/getqq").setViewName("getqq");
//				registry.addViewController("/index").setViewName("getqq");
	}
}
