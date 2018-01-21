package com.sberbank.cms.ui.sidebar.planning.campaigns;

import com.sberbank.cms.backend.domain.model.Campaign;
import com.sberbank.cms.backend.domain.services.CampaignRepository;
import com.sberbank.cms.backend.domain.services.ContentKindRepository;
import com.sberbank.cms.ui.common.components.AddButton;
import com.sberbank.cms.ui.common.components.KindSelector;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.vaadin.spring.sidebar.annotation.SideBarItem;
import org.vaadin.spring.sidebar.annotation.VaadinFontIcon;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import static com.sberbank.cms.backend.security.Role.ROLE_ADMIN;
import static com.sberbank.cms.backend.security.Role.ROLE_OFFICER;
import static com.sberbank.cms.ui.sidebar.Sections.CONTENT;
import static com.vaadin.icons.VaadinIcons.SHOP;

@Secured({ROLE_ADMIN, ROLE_OFFICER})
@SpringView(name = CampaignsView.VIEW_NAME)
@SideBarItem(sectionId = CONTENT, caption = "Campaigns", order = 1)
@VaadinFontIcon(SHOP)
public class CampaignsView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "campaigns";

    private KindSelector kindSelector;
    private final CampaignRepository repo;
    private final ContentKindRepository kindRepo;
    private final Button add = new AddButton(e -> openEditFormFor(kindSelector.selected()));

    private final MGrid<Campaign> list = new MGrid<>(Campaign.class)
            .withProperties("name", "startDate", "endDate", "data")
            .withColumnHeaders("name", "start", "end", "data")
            .withFullSize();

    private final TextField search = new MTextField("", e -> refreshList(e.getValue()))
            .withPlaceholder("Search by name...");

    public CampaignsView(CampaignRepository repo, ContentKindRepository kindRepo) {
        this.repo = repo;
        this.kindRepo = kindRepo;
    }

    @Override
    public void enter(ViewChangeEvent event) {
        kindSelector = new KindSelector(kindRepo, event.getParameters());
        list.addSelectionListener(e -> openEditFormFor(e.getFirstSelectedItem().get().getId()));
        refreshList(search.getValue());

        addComponents(
                kindSelector,
                new MHorizontalLayout(search, add).expand(search),
                list
        );
    }

    private void refreshList(String name) {
        final String selected = kindSelector.selected();
        list.setDataProvider(
                (sortOrder, offset, limit) -> repo.findByContentKindAndNameLike(
                        selected,
                        name,
                        new PageRequest(offset / limit, limit)
                ).stream(),
                () -> repo.countByContentKindAndNameLike(selected, name)
        );
    }

    private void openEditFormFor(Object param) {
        getUI().getNavigator().navigateTo(CampaignEditView.VIEW_NAME + "/" + param);
    }
}