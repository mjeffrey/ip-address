package be.sa.iplocation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties
@EnableScheduling
public class IpLocationApplication {
	public static void main(String[] args) {
		SpringApplication.run(IpLocationApplication.class, args);
	}
}
