package com.sberbank.cms.ui.sidebar.planning.offers;

import com.sberbank.cms.backend.content.Campaign;
import com.sberbank.cms.backend.content.CampaignRepository;
import com.sberbank.cms.backend.content.ContentKindRepository;
import com.sberbank.cms.ui.sidebar.Sections;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.spring.sidebar.annotation.SideBarItem;
import org.vaadin.spring.sidebar.annotation.VaadinFontIcon;
import org.vaadin.viritin.button.ConfirmButton;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import static com.sberbank.cms.security.Role.ROLE_ADMIN;
import static com.sberbank.cms.security.Role.ROLE_OFFICER;
import static com.vaadin.icons.VaadinIcons.*;
import static com.vaadin.ui.Grid.SelectionMode.NONE;

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
    private final EventBus.UIEventBus eventBus;
    private CampaignForm form;

    private MGrid<Campaign> list = new MGrid<>(Campaign.class)
            .withProperties("id", "contentKind", "data")
            .withColumnHeaders("id", "Kind", "Data")
            .withFullSize();

    private MTextField filterByName = new MTextField().withPlaceholder("Filter by name");
    private Button addNew = new MButton(VaadinIcons.PLUS, click -> edit(new Campaign()));

    public CampaignsView(CampaignRepository repo, EventBus.UIEventBus eventBus, ContentKindRepository kindRepo) {
        this.repo = repo;
        this.kindRepo = kindRepo;
        this.eventBus = eventBus;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        list.setSelectionMode(NONE);
        list.addComponentColumn(campaign -> new HorizontalLayout(
                        new MButton(PENCIL, click -> edit(campaign)),
                        new ConfirmButton(TRASH, "Are you sure you want to delete the entry?", () -> {
                            repo.delete(campaign);
                            listEntities();
                        })
                )
        );
        addComponent(
                new MVerticalLayout(
                        new MHorizontalLayout(filterByName, addNew).expand(filterByName)
                ).expand(
                        new MHorizontalLayout().expand(list)
                )
        );
        listEntities();

        filterByName.addValueChangeListener(e -> listEntities(e.getValue()));

        eventBus.subscribe(this);
        setSizeFull();
    }

    private void listEntities() {
        listEntities(filterByName.getValue());
    }

    private void listEntities(String nameFilter) {
        String likeFilter = "%" + nameFilter + "%";

        list.setDataProvider(
                (sort, offset, limit) -> {
                    final Page<Campaign> page = repo.findAll(
                            new PageRequest(
                                    offset / limit,
                                    limit,
                                    sort.isEmpty() || sort.get(0).getDirection() == SortDirection.ASCENDING
                                            ? Sort.Direction.ASC : Sort.Direction.DESC,
                                    sort.isEmpty() ? "id" : sort.get(0).getSorted()
                            )
                    );
                    return page.getContent().stream();
                },
                () -> (int) repo.count()
        );
    }

    private void edit(final Campaign campaign) {
        form = new CampaignForm(repo, kindRepo, eventBus);
        form.setEntity(campaign);
        form.openInModalPopup();
    }

    @EventBusListenerMethod(scope = EventScope.UI)
    public void onModified(Campaign event) {
        listEntities();
        form.closePopup();
    }
}