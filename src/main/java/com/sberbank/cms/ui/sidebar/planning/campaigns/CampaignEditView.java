package com.sberbank.cms.ui.sidebar.planning.campaigns;

import com.sberbank.cms.backend.domain.model.Campaign;
import com.sberbank.cms.backend.domain.model.ContentField;
import com.sberbank.cms.backend.domain.services.CampaignRepository;
import com.sberbank.cms.backend.domain.services.ContentKindRepository;
import com.sberbank.cms.backend.domain.services.PlaceRepository;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
import com.vaadin.data.BindingValidationStatus;
import com.vaadin.data.HasValue;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MFormLayout;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.vaadin.data.provider.DataProvider.ofCollection;
import static com.vaadin.ui.themes.ValoTheme.BUTTON_PRIMARY;
import static java.lang.Long.parseLong;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@SpringView(name = CampaignEditView.VIEW_NAME)
public class CampaignEditView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "campaign";

    private final TextField name = new MTextField("Name").withFullWidth();
    private final DateTimeField startDate = new DateTimeField("Start date", LocalDateTime.now());
    private final ListSelect<String> places = new ListSelect<>();
    private final Layout form = new MFormLayout(name, startDate, places).withFullWidth();
    private CampaignRepository campaignRepo;
    private PlaceRepository placeRepo;
    private ContentKindRepository contentKindRepo;
    private BeanValidationBinder<Campaign> binder;

    @Autowired
    public CampaignEditView(CampaignRepository campaignRepo, PlaceRepository placeRepo, ContentKindRepository contentKindRepo) {
        this.campaignRepo = campaignRepo;
        this.placeRepo = placeRepo;
        this.contentKindRepo = contentKindRepo;
    }

    @PostConstruct
    public void init() {
        binder = new BeanValidationBinder<>(Campaign.class);
        binder.bindInstanceFields(this);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        //FIXME temporary coupling in next to lines
        configurePlaces();
        enterView(event.getParameters());

        addComponents(
                form,
                new MCssLayout(
                        new MButton("Save", e -> saveAndBack()).withStyleName(BUTTON_PRIMARY),
                        new MButton("Cancel", e -> back())
                ).withFullWidth().withStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP)
        );
    }

    private void addKindFields(Binder<Campaign> binder) {
        Campaign campaign = getCampaign();
        if (campaign != null && campaign.getKind() != null) {
            form.addComponents(
                    campaign.getKind().getFields().stream()
                            .map(field -> field.getType().ui(field.getName(), binder))
                            .toArray(AbstractField[]::new)
            );
        }
    }

    private void back() {
        getUI().getNavigator().navigateTo(CampaignsView.VIEW_NAME + "/" + getCampaign().getKind().getStrId());
    }

    private Campaign getCampaign() {
        return binder.getBean();
    }

    private void configurePlaces() {
        places.setCaption("Places");
        places.setRows(3);
        places.setDataProvider(ofCollection(placeRepo.findAllNames()));
    }

    private void saveAndBack() {
        Optional<HasValue<?>> firstErrorField = validate().findFirst();
        if (firstErrorField.isPresent()) {
            ((Focusable) firstErrorField.get()).focus();
        } else {
            saveBean();
            back();
        }
    }

    private void saveBean() {
        Campaign campaign = getCampaign();
        campaign.setData(
                campaign.getData().entrySet().stream().
                        filter(notKindFieldsOrPlaces(campaign)).
                        collect(toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
        campaignRepo.save(campaign);
    }

    private Predicate<Map.Entry<String, Object>> notKindFieldsOrPlaces(Campaign campaign) {
        List<String> kindFields = campaign.getKind().getFields().stream().
                map(ContentField::getName).
                collect(toList());
        return entry -> kindFields.contains(entry.getKey()) || places.getCaption().equalsIgnoreCase(entry.getKey());
    }

    private void enterView(String param) {
        Campaign campaign = new Campaign();
        try {
            campaign = campaignRepo.findOne(parseLong(param));
            if (campaign == null) {
                showNotFound();
                return;
            }
        } catch (NumberFormatException e) {
            campaign.setKind(contentKindRepo.findByStrId(param));
        }
        setBean(campaign);
    }

    private void setBean(Campaign bean) {
        binder.setBean(bean);
        binder.forField(places).bind(
                campaign -> getPlacesFrom(campaign),
                (campaign, val) -> {
                    Map<String, Object> data = campaign.getData();
                    data.put(places.getCaption(), new ArrayList<>(val));
                    campaign.setData(data);
                }
        );
        addKindFields(binder);
    }

    private Set<String> getPlacesFrom(Campaign c) {
        if (c.getData() != null && !c.getData().isEmpty()) {
            Object places = c.getData().get(this.places.getCaption());
            if (places != null) {
                return new HashSet<>((List<String>) places);
            }
        }
        return new HashSet<>();
    }

    private void showNotFound() {
        removeAllComponents();
        addComponent(new Label("Campaign not found"));
    }

    private Stream<HasValue<?>> validate() {
        return binder.validate().getFieldValidationErrors().stream().map(BindingValidationStatus::getField);
    }
}