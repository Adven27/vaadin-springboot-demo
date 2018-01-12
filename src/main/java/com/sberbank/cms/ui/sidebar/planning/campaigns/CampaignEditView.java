package com.sberbank.cms.ui.sidebar.planning.campaigns;

import com.sberbank.cms.backend.content.*;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
import com.vaadin.data.BindingValidationStatus;
import com.vaadin.data.HasValue;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.vaadin.data.provider.DataProvider.ofCollection;
import static java.lang.Long.parseLong;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@SpringView(name = CampaignEditView.VIEW_NAME)
public class CampaignEditView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "campaign";
    private BeanValidationBinder<Campaign> binder;

    private TextField name = new MTextField("Name").withFullWidth();
    private DateTimeField startDate = new DateTimeField("Start date", LocalDateTime.now());
    private final ListSelect<String> places = new ListSelect<>();
    private Button cancel;
    private CampaignRepository campaignRepo;
    private PlaceRepository placeRepo;
    private ContentKindRepository contentKindRepo;
    private final Layout commonFields = new MVerticalLayout(new MLabel("Common").withStyleName("header").withFullWidth());
    private final Layout dataFields = new MVerticalLayout(new MLabel("Content").withStyleName("header").withFullWidth());

    @Autowired
    public CampaignEditView(CampaignRepository campaignRepo, PlaceRepository placeRepo, ContentKindRepository contentKindRepo) {
        this.campaignRepo = campaignRepo;
        this.placeRepo = placeRepo;
        this.contentKindRepo = contentKindRepo;
    }

    @PostConstruct
    public void init() {
        binder = new BeanValidationBinder<>(Campaign.class);
//		binder.setRequiredConfigurator(null);
        binder.bindInstanceFields(this);
    }

    private void refreshFieldsFor(Binder<Campaign> binder) {
        dataFields.removeAllComponents();
        Campaign campaign = this.binder.getBean();
        if (campaign != null && campaign.getContentKind() != null) {
            dataFields.addComponents(
                    campaign.getContentKind().getFields().stream().
                            map(field -> field.getType().ui(field.getName(), binder)).
                            toArray(AbstractField[]::new)
            );
        }
    }

    @Override
    public void enter(ViewChangeEvent event) {

        //FIXME temporary coupling in next to lines
        configurePlaces();

        enterView(event.getParameters());

        commonFields.addComponents(name, startDate, places);
        addComponents(
                commonFields,
                dataFields,
                new MCssLayout(
                        new MButton("Save", e -> saveCustomer()),
                        new MButton("Cancel", e -> back())
                ).withFullWidth()
        );
    }

    private void back() {
        getUI().getNavigator().navigateTo(CmpsView.VIEW_NAME + "/" + binder.getBean().getContentKind().getStrId());
    }

    private void configurePlaces() {
        places.setCaption("places");
        places.setRows(3);
        places.setDataProvider(ofCollection(placeRepo.findAllNames()));
    }

    private void saveCustomer() {
        Campaign campaign = binder.getBean();
        campaign.setData(
                campaign.getData().entrySet().stream().
                        filter(notKindFieldsOrPlaces(campaign)).
                        collect(toMap(Map.Entry::getKey, Map.Entry::getValue))
        );

        Optional<HasValue<?>> firstErrorField = validate().findFirst();
        if (firstErrorField.isPresent()) {
            ((Focusable) firstErrorField.get()).focus();
            return;
        }
        campaignRepo.save(campaign);
        back();
    }

    private Predicate<Map.Entry<String, Object>> notKindFieldsOrPlaces(Campaign campaign) {
        List<String> kindFields = campaign.getContentKind().getFields().stream().
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
            campaign.setContentKind(contentKindRepo.findByStrId(param));
        }
        setBean(campaign);
    }

    private void setBean(Campaign bean) {
        binder.setBean(bean);
        binder.forField(places).bind(
                campaign -> setter(campaign),
                (campaign, val) -> {
                    Map<String, Object> data = campaign.getData();
                    data.put(places.getCaption(), new ArrayList<>(val));
                    campaign.setData(data);
                }
        );
        refreshFieldsFor(binder);
    }

    @NotNull
    private HashSet<String> setter(Campaign c) {
        return c.getData() == null || c.getData().isEmpty()
                ? new HashSet<>()
                : new HashSet<>((List<String>) c.getData().get(places.getCaption()));
    }

    private void showNotFound() {
        removeAllComponents();
        addComponent(new Label("Campaign not found"));
    }

    private Stream<HasValue<?>> validate() {
        return binder.validate().getFieldValidationErrors().stream().map(BindingValidationStatus::getField);
    }
}