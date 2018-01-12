package com.sberbank.cms.ui.sidebar.planning.offers;

import com.sberbank.cms.backend.content.*;
import com.sberbank.cms.ui.sidebar.Sections;
import com.vaadin.data.Binder;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.access.annotation.Secured;
import org.vaadin.spring.sidebar.annotation.SideBarItem;
import org.vaadin.spring.sidebar.annotation.VaadinFontIcon;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static com.sberbank.cms.security.Role.ROLE_ADMIN;
import static com.sberbank.cms.security.Role.ROLE_OFFICER;
import static com.vaadin.data.provider.DataProvider.ofCollection;
import static com.vaadin.icons.VaadinIcons.SHOP;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Secured({ROLE_ADMIN, ROLE_OFFICER})
@SpringView(name = CampaignsView.VIEW_NAME)
@SideBarItem(sectionId = Sections.PLANNING, caption = "Campaigns", order = 1)
@VaadinFontIcon(SHOP)
@ViewScope
public class CampaignsView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "campaigns";
    private static final long serialVersionUID = 2217814051618370412L;

    private final CampaignRepository repo;
    private final ContentKindRepository kindRepo;
    private PlaceRepository placeRepo;

    private Campaign campaign;
    private Binder<Campaign> binder;
    private MTextField name = new MTextField("Name").withFullWidth();
    private DateTimeField startDate = new DateTimeField("Start date", LocalDateTime.now());
    private final ListSelect<String> places = new ListSelect<>();
    private final Panel panel = new Panel();
    private final Layout table = new MVerticalLayout().withMargin(false);
    private final Layout commonFields = new MFormLayout();
    private final Layout dataFields = new MFormLayout();

    private MGrid<Campaign> grid = new MGrid<>(Campaign.class)
            .withProperties("id", "name", "startDate", "endDate", "data")
            .withColumnHeaders("id", "name", "start", "end", "data")
            .withFullSize();

    private final Button save = new MButton("Save", e -> saveCustomer());


    public CampaignsView(CampaignRepository repo, ContentKindRepository kindRepo, PlaceRepository placeRepo) {
        this.repo = repo;
        this.kindRepo = kindRepo;
        this.placeRepo = placeRepo;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        grid.addSelectionListener(e -> updateForm());

        HorizontalLayout kindsLayout = new MHorizontalLayout();
        kindRepo.findAll().forEach(
                kind -> kindsLayout.addComponent(
                        new MButton(
                                kind.getName(),
                                click -> updateGrid(kind)
                        )
                )
        );
        table.addComponents(kindsLayout, grid);


        configurePlaces();

        commonFields.addComponents(name, startDate, places);

        panel.setSizeUndefined();
        panel.setVisible(false);
        panel.setContent(
                new MVerticalLayout(
                        commonFields,
                        dataFields,
                        new MHorizontalLayout(
                                save,
                                new Button("Cancel", e -> hidePanel())
                        )
                )
        );

        addComponent(new MHorizontalLayout(table, panel).withMargin(false));
    }

    private void updateGrid(ContentKind kind) {
        grid.setDataProvider(ofCollection(repo.findByContentKind(kind.getStrId())));
    }

    private void updateForm() {
        if (grid.asSingleSelect().isEmpty()) {
            hidePanel();
        } else {
            showPanel();
        }
    }

    private void showPanel() {
        campaign = grid.asSingleSelect().getValue();
        refreshBinder(campaign);
        table.setVisible(false);
        panel.setVisible(true);
        panel.setCaption(campaign.getContentKind().getName());
    }

    private void hidePanel() {
        panel.setVisible(false);
        table.setVisible(true);
    }

    private void refreshBinder(Campaign bean) {
        binder = new Binder<>(Campaign.class);
        binder.forMemberField(name).withValidator(new BeanValidator(Campaign.class,"name"));

        binder.bindInstanceFields(this);
        binder.setBean(bean);

        binder.forField(places).bind(
                campaign -> new HashSet<>((List<String>) campaign.getData().get(places.getCaption())),
                (campaign, val) -> {
                    Map<String, Object> data = campaign.getData();
                    data.put(places.getCaption(), new ArrayList<>(val));
                    campaign.setData(data);
                }
        );

        binder.addStatusChangeListener(e -> save.setEnabled(!e.hasValidationErrors() && e.getBinder().isValid()));

        refreshFieldsFor(binder);
    }

    private void refreshFieldsFor(Binder<Campaign> binder) {
        dataFields.removeAllComponents();
        if (campaign != null && campaign.getContentKind() != null) {
            dataFields.addComponents(
                    campaign.getContentKind().getFields().stream().
                            map(field -> field.getType().ui(field.getName(), binder)).
                            toArray(AbstractField[]::new)
            );
        }
    }

    private void saveCustomer() {
        campaign.setData(
                campaign.getData().entrySet().stream().
                        filter(notKindFieldsOrPlaces()).
                        collect(toMap(Map.Entry::getKey, Map.Entry::getValue))
        );

        repo.save(campaign);
        updateGrid(campaign.getContentKind());
        hidePanel();
    }

    @NotNull
    private Predicate<Map.Entry<String, Object>> notKindFieldsOrPlaces() {
        List<String> kindFields = campaign.getContentKind().getFields().stream().
                map(ContentField::getName).
                collect(toList());
        return entry -> kindFields.contains(entry.getKey()) || places.getCaption().equalsIgnoreCase(entry.getKey());
    }

    private void configurePlaces() {
        places.setCaption("places");
        places.setRows(3);
        places.setDataProvider(ofCollection(placeRepo.findAllNames()));
    }
}