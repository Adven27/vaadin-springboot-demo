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

import java.util.Date;
import java.util.List;

import static com.sberbank.cms.backend.content.FieldType.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;

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
            addCampaigns(campaignRepo, kindRepo);
        };
    }

    private void addUsers(UserRepository repo, PasswordEncoder passEncoder) {
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
    }

    private void addCampaigns(CampaignRepository campaignRepo, ContentKindRepository kindRepo) {
        ContentKind kind = kind();
        kindRepo.save(kind);
        campaignRepo.save(Campaign.builder().contentKind(kind).data(singletonMap("text field", "some value")).build());

        LOG.info("Campaign found with findByContentKind('" + kind + "'):");
        LOG.info("--------------------------------------------");
        for (Campaign test : campaignRepo.findByContentKind(kind.getStrId())) {
            LOG.info(test.toString());
        }
        LOG.info("");
    }

    private ContentKind kind() {
        ContentKind kind = ContentKind.builder().strId("offer").name("Offers").creationDate(new Date()).build();
        ContentField.ContentFieldBuilder builder = ContentField.builder().contentKind(kind);
        List<ContentField> fields = asList(
                builder.name("text field").type(TEXT).build(),
                builder.name("rich field").type(RICH_TEXT).build(),
                builder.name("bool field").type(BOOL).build(),
                builder.name("date field").type(DATE).build()
        );
        kind.setFields(fields);
        return kind;
    }
}