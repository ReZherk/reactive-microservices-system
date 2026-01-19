package com.ReZherk.microservice_reservations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableDiscoveryClient
@SpringBootApplication
@EnableScheduling
public class MicroserviceReservationsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceReservationsApplication.class, args);
	}

}
