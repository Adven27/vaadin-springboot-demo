package com.sberbank.cms.ui.sidebar.planning.campaigns;

import com.sberbank.cms.backend.content.Campaign;
import com.sberbank.cms.backend.content.CampaignRepository;
import com.sberbank.cms.backend.content.ContentKindRepository;
import com.sberbank.cms.ui.common.forms.AddButton;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.vaadin.spring.sidebar.annotation.SideBarItem;
import org.vaadin.spring.sidebar.annotation.VaadinFontIcon;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import static com.sberbank.cms.security.Role.ROLE_ADMIN;
import static com.sberbank.cms.security.Role.ROLE_OFFICER;
import static com.sberbank.cms.ui.sidebar.Sections.PLANNING;
import static com.vaadin.icons.VaadinIcons.SHOP;
import static com.vaadin.ui.themes.ValoTheme.BUTTON_PRIMARY;
import static com.vaadin.ui.themes.ValoTheme.LAYOUT_COMPONENT_GROUP;

@Secured({ROLE_ADMIN, ROLE_OFFICER})
@SpringView(name = CampaignsView.VIEW_NAME)
@SideBarItem(sectionId = PLANNING, caption = "Campaigns", order = 1)
@VaadinFontIcon(SHOP)
public class CampaignsView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "campaigns";

    private String kindStrId;
    private final CampaignRepository repo;
    private final ContentKindRepository kindRepo;
    private final Button addButton = new AddButton(e -> openEditFormFor(kindStrId));
    private final MGrid<Campaign> list = new MGrid<>(Campaign.class)
            .withProperties("name", "startDate", "endDate", "data")
            .withColumnHeaders("name", "start", "end", "data")
            .withFullSize();
    private final TextField search = new MTextField()
            .withPlaceholder("Search by name...")
            .withValueChangeListener(e -> refreshList(e.getValue()));


    @Autowired
    public CampaignsView(CampaignRepository repo, ContentKindRepository kindRepo) {
        this.repo = repo;
        this.kindRepo = kindRepo;
    }

    @Override
    public void enter(ViewChangeEvent event) {
        list.addSelectionListener(e -> openEditFormFor(e.getFirstSelectedItem().get().getId()));
        kindStrId = event.getParameters();
        refreshList(search.getValue());

        addComponents(
                kindsLayout(),
                new MHorizontalLayout(search, addButton).expand(search),
                list
        );
    }

    private void openEditFormFor(Object param) {
        getUI().getNavigator().navigateTo(CampaignEditView.VIEW_NAME + "/" + param);
    }

    private void refreshList(String name) {
        list.setDataProvider(
                (sortOrder, offset, limit) -> repo.findByContentKindAndNameLike(
                        kindStrId,
                        name,
                        new PageRequest(offset / limit, limit)
                ).stream(),
                () -> repo.countByContentKindAndNameLike(kindStrId, name)
        );
    }

    private Layout kindsLayout() {
        Layout layout = new MCssLayout().withStyleName(LAYOUT_COMPONENT_GROUP);
        kindRepo.findAll().forEach(
                kind -> layout.addComponent(
                        new MButton(
                                kind.getName(),
                                click -> getUI().getNavigator().navigateTo(VIEW_NAME + "/" + kind.getStrId())
                        ).withStyleName(kind.getStrId().equals(kindStrId) ? BUTTON_PRIMARY : "")
                )
        );
        return layout;
    }
}