package com.sberbank.cms;

import com.sberbank.cms.backend.UserInfo;
import com.sberbank.cms.backend.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.vaadin.spring.sidebar.annotation.EnableSideBar;

@EnableSideBar
@SpringBootApplication
public class Application {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);
    public static final String LOGIN_URL = "/login.html";
    public static final String LOGIN_PROCESSING_URL = "/login";

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner loadData(UserRepository repository, PasswordEncoder passwordEncoder) {
        return (args) -> {
            // save a couple of customers
            repository.save(new UserInfo("t", "test", passwordEncoder.encode("t"), "admin"));
            repository.save(new UserInfo("o", "test 2", passwordEncoder.encode("o"), "officer"));

            // fetch all customers
            LOG.info("Customers found with findAll():");
            LOG.info("-------------------------------");
            for (UserInfo user : repository.findAll()) {
                LOG.info(user.toString());
            }
            LOG.info("");

            // fetch an individual customer by ID
            UserInfo user = repository.findOne(1L);
            LOG.info("Customer found with findOne(1L):");
            LOG.info("--------------------------------");
            LOG.info(user.toString());
            LOG.info("");

            // fetch customers by last name
            LOG.info("Customer found with findByLastNameStartsWithIgnoreCase('Bauer'):");
            LOG.info("--------------------------------------------");
            for (UserInfo test : repository
                    .findByLoginLikeIgnoreCaseOrNameLikeIgnoreCase("test", "test")) {
                LOG.info(test.toString());
            }
            LOG.info("");
        };
    }
}