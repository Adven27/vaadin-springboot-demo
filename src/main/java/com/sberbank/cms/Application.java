package com.sberbank.cms;

import com.sberbank.cms.backend.Customer;
import com.sberbank.cms.backend.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.vaadin.spring.sidebar.annotation.EnableSideBar;

@EnableSideBar
@SpringBootApplication
public class Application {
	private static final Logger LOG = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner loadData(CustomerRepository repository) {
		return (args) -> {
			// save a couple of customers
			repository.save(new Customer("Jack", "Bauer"));
			repository.save(new Customer("Chloe", "O'Brian"));
			repository.save(new Customer("Kim", "Bauer"));
			repository.save(new Customer("David", "Palmer"));
			repository.save(new Customer("Michelle", "Dessler"));

			// fetch all customers
			LOG.info("Customers found with findAll():");
			LOG.info("-------------------------------");
			for (Customer customer : repository.findAll()) {
				LOG.info(customer.toString());
			}
			LOG.info("");

			// fetch an individual customer by ID
			Customer customer = repository.findOne(1L);
			LOG.info("Customer found with findOne(1L):");
			LOG.info("--------------------------------");
			LOG.info(customer.toString());
			LOG.info("");

			// fetch customers by last name
			LOG.info("Customer found with findByLastNameStartsWithIgnoreCase('Bauer'):");
			LOG.info("--------------------------------------------");
			for (Customer bauer : repository
					.findByLastNameStartsWithIgnoreCase("Bauer")) {
				LOG.info(bauer.toString());
			}
			LOG.info("");
		};
	}
}
