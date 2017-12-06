package com.sberbank.cms;

import com.sberbank.cms.backend.Role;
import com.sberbank.cms.backend.UserInfo;
import com.sberbank.cms.backend.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.vaadin.spring.security.annotation.EnableVaadinSharedSecurity;
import org.vaadin.spring.sidebar.annotation.EnableSideBar;

@EnableSideBar
@SpringBootApplication
public class Application {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @EnableWebSecurity
    @EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, proxyTargetClass = true)
    @EnableVaadinSharedSecurity
    @Configuration
    public static class SecurityConfig extends WebSecurityConfigurerAdapter {
        private static final String LOGIN_URL = "/login.html";
        private static final String LOGIN_PROCESSING_URL = "/login";

        @Autowired
        private UserDetailsService userDetailsService;
        @Autowired
        private PasswordEncoder passwordEncoder;

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            super.configure(auth);
            auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
//        auth.inMemoryAuthentication().withUser("a").password("a").roles("admin");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable().
                authorizeRequests().
                    anyRequest().hasAnyRole(Role.ALL).
                    and().
                formLogin().
                    loginPage(LOGIN_URL).
                    loginProcessingUrl(LOGIN_PROCESSING_URL).
                    permitAll();
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers("/VAADIN/**");
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    @Bean
    public CommandLineRunner loadData(UserRepository repo, PasswordEncoder passEncoder) {
        return (args) -> {
            final String op = "op";
            final String admin = "admin";
            repo.save(new UserInfo(admin, admin, passEncoder.encode(admin), Role.ADMIN));
            repo.save(new UserInfo(op, "officer", passEncoder.encode(op), Role.OFFICER));

            LOG.info("Customers found with findAll():");
            LOG.info("-------------------------------");
            for (UserInfo user : repo.findAll()) {
                LOG.info(user.toString());
            }
            LOG.info("");

            LOG.info("Customer found with findByLastNameStartsWithIgnoreCase('" + op + "'):");
            LOG.info("--------------------------------------------");
            for (UserInfo test : repo
                    .findByLoginLikeIgnoreCaseOrNameLikeIgnoreCase(op, op)) {
                LOG.info(test.toString());
            }
            LOG.info("");
        };
    }
}