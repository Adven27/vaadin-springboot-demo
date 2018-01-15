package com.sberbank.cms;

import com.sberbank.cms.backend.content.*;
import com.sberbank.cms.security.Role;
import com.sberbank.cms.security.UserInfo;
import com.sberbank.cms.security.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.vaadin.spring.sidebar.annotation.EnableSideBar;

import java.time.LocalDateTime;
import java.util.Date;

import static com.sberbank.cms.backend.content.FieldType.RICH_TEXT;
import static com.sberbank.cms.backend.content.FieldType.TEXT;
import static java.util.Arrays.asList;

@EnableSideBar
@SpringBootApplication
public class Application {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner loadData(UserRepository repo, PasswordEncoder passEncoder,
                                      CampaignRepository campaignRepo, ContentKindRepository kindRepo) {
        return (args) -> {
            addUsers(repo, passEncoder);
            ContentKind kind = tipsKind();
            kindRepo.save(kind);
            kindRepo.save(offersKind());
            kindRepo.save(vectorsKind());

            Campaign entity = new Campaign();
            entity.setKind(kind);
            entity.setStartDate(LocalDateTime.now());
            entity.setName("Test");
            campaignRepo.save(entity);
        };
    }

    private void addUsers(UserRepository repo, PasswordEncoder passEncoder) {
        final String op = "op";
        final String admin = "admin";
        repo.save(new UserInfo(admin, admin, passEncoder.encode(admin), Role.ADMIN));
        repo.save(new UserInfo(op, "officer", passEncoder.encode(op), Role.OFFICER));

        LOG.info("Users found with findAll():");
        LOG.info("-------------------------------");
        repo.findAll().forEach(user -> LOG.info(user.toString()));
        LOG.info("");
    }

    private ContentKind tipsKind() {
        ContentKind kind = ContentKind.builder().strId("tips").name("Tips").creationDate(new Date()).build();
        ContentField.ContentFieldBuilder builder = ContentField.builder().kind(kind);
        kind.setFields(asList(
                builder.name("title").type(TEXT).build(),
                builder.name("desc").type(RICH_TEXT).build()
        ));
        return kind;
    }

    private ContentKind offersKind() {
        ContentKind kind = ContentKind.builder().strId("offers").name("Offers").creationDate(new Date()).build();
        ContentField.ContentFieldBuilder builder = ContentField.builder().kind(kind);
        kind.setFields(asList(
                builder.name("title").type(TEXT).build(),
                builder.name("desc").type(RICH_TEXT).build()
        ));
        return kind;
    }

    private ContentKind vectorsKind() {
        ContentKind kind = ContentKind.builder().strId("vectors").name("Vectors").creationDate(new Date()).build();
        ContentField.ContentFieldBuilder builder = ContentField.builder().kind(kind);
        kind.setFields(asList(
                builder.name("name").type(TEXT).build(),
                builder.name("value").type(TEXT).build()
        ));
        return kind;
    }
}