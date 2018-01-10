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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.sberbank.cms.backend.content.FieldType.*;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

@EnableSideBar
@SpringBootApplication
public class Application {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner loadData(UserRepository repo, PasswordEncoder passEncoder,
                                      CampaignRepository campaignRepo, ContentKindRepository kindRepo,
                                      PlaceRepository placeRepo) {
        return (args) -> {
            addUsers(repo, passEncoder);
            addPlaces(placeRepo, "place 1", "place 2");
            addCampaigns(campaignRepo, kindRepo, "offer");
            addCampaigns(campaignRepo, kindRepo, "vector", "place 1", "place 2");
        };
    }

    private void addUsers(UserRepository repo, PasswordEncoder passEncoder) {
        final String op = "op";
        final String admin = "admin";
        repo.save(new UserInfo(admin, admin, passEncoder.encode(admin), Role.ADMIN));
        repo.save(new UserInfo(op, "officer", passEncoder.encode(op), Role.OFFICER));

        LOG.info("Customers found with findAll():");
        LOG.info("-------------------------------");
        repo.findAll().forEach(user -> LOG.info(user.toString()));
        LOG.info("");

        LOG.info("Customer found with findByLastNameStartsWithIgnoreCase('" + op + "'):");
        LOG.info("--------------------------------------------");
        repo.findByLoginLikeIgnoreCaseOrNameLikeIgnoreCase(op, op).forEach(user -> LOG.info(user.toString()));
        LOG.info("");
    }

    private void addCampaigns(CampaignRepository campaignRepo, ContentKindRepository kindRepo, String name, String... places) {
        ContentKind kind = kind(name);
        kindRepo.save(kind);
        Map<String, Object> data = new HashMap<>();
        data.put("text field", "some value");
        data.put("places", asList(places));

        campaignRepo.save(Campaign.builder().contentKind(kind).data(data).name(name).build());

        LOG.info("Campaign found with findByContentKind('" + kind + "'):");
        LOG.info("--------------------------------------------");
        campaignRepo.findByContentKind(kind.getStrId()).forEach(campaign -> LOG.info(campaign.toString()));
        LOG.info("");
    }

    private void addPlaces(PlaceRepository repo, String... names) {
        repo.save(Stream.of(names).map(Place::new).collect(toList()));

        LOG.info("Places found with findAllNames():");
        LOG.info("--------------------------------------------");
        repo.findAllNames().forEach(LOG::info);
        LOG.info("");
    }

    private ContentKind kind(String name) {
        ContentKind kind = ContentKind.builder().strId(name).name(name + "s").creationDate(new Date()).build();
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