package com.sberbank.cms.ui.sidebar.planning.offers;

import com.sberbank.cms.backend.content.Campaign;
import com.sberbank.cms.backend.content.CampaignRepository;
import com.sberbank.cms.backend.content.ContentKindRepository;
import com.sberbank.cms.backend.content.PlaceRepository;
import com.sberbank.cms.ui.common.forms.CustomGrid;
import com.sberbank.cms.ui.sidebar.Sections;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.sidebar.annotation.SideBarItem;
import org.vaadin.spring.sidebar.annotation.VaadinFontIcon;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.grid.MGrid;

import static com.sberbank.cms.security.Role.ROLE_ADMIN;
import static com.sberbank.cms.security.Role.ROLE_OFFICER;
import static com.vaadin.icons.VaadinIcons.SHOP;

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
    private final PlaceRepository placeRepo;
    private final EventBus.UIEventBus eventBus;

    public CampaignsView(CampaignRepository repo, EventBus.UIEventBus eventBus, ContentKindRepository kindRepo, PlaceRepository placeRepo) {
        this.repo = repo;
        this.kindRepo = kindRepo;
        this.eventBus = eventBus;
        this.placeRepo = placeRepo;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        addComponent(new CustomGrid<Campaign>(eventBus, Campaign.class) {

            @Override
            public Campaign addRow() {
                return new Campaign();
            }

            @Override
            public MGrid<Campaign> grid() {
                return new MGrid<>(Campaign.class)
                        .withProperties("id", "contentKind", "data")
                        .withColumnHeaders("id", "Kind", "Data")
                        .withFullSize();
            }

            @Override
            public void deleteRow(Campaign row) {
                repo.delete(row);
            }

            @Override
            public void listEntities(Grid<Campaign> grid, String nameFilter) {
                String likeFilter = "%" + nameFilter + "%";
                grid.setDataProvider(
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

            @Override
            protected AbstractForm form() {
                return new CampaignForm(repo, kindRepo, eventBus, placeRepo);
            }
        });
    }
}