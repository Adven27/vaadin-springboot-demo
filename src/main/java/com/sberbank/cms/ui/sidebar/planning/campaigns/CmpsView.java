package com.sberbank.cms.ui.sidebar.planning.campaigns;

import com.sberbank.cms.backend.content.Campaign;
import com.sberbank.cms.backend.content.CampaignRepository;
import com.sberbank.cms.backend.content.ContentKindRepository;
import com.sberbank.cms.ui.sidebar.Sections;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.vaadin.spring.sidebar.annotation.SideBarItem;
import org.vaadin.spring.sidebar.annotation.VaadinFontIcon;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import javax.annotation.PostConstruct;

import static com.sberbank.cms.security.Role.ROLE_ADMIN;
import static com.sberbank.cms.security.Role.ROLE_OFFICER;
import static com.vaadin.data.provider.DataProvider.ofCollection;
import static com.vaadin.icons.VaadinIcons.SHOP;

@Secured({ROLE_ADMIN, ROLE_OFFICER})
@SpringView(name = CmpsView.VIEW_NAME)
@SideBarItem(sectionId = Sections.PLANNING, caption = "Cmps", order = 1)
@VaadinFontIcon(SHOP)
public class CmpsView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "cmps";
    private final CampaignRepository repo;
    private final ContentKindRepository kindRepo;
    protected Button newOrder = new MButton("New");

    private MGrid<Campaign> list = new MGrid<>(Campaign.class)
            .withProperties("id", "name", "startDate", "endDate", "data")
            .withColumnHeaders("id", "name", "start", "end", "data")
            .withFullSize();
    private String kindStrId;

    @Autowired
    public CmpsView(CampaignRepository repo, ContentKindRepository kindRepo) {
        this.repo = repo;
        this.kindRepo = kindRepo;
    }

    @PostConstruct
    public void init() {
        list.addSelectionListener(e -> selectedOrder(e.getFirstSelectedItem().get()));
        newOrder.addClickListener(e -> newOrder());
    }

    private void selectedOrder(Campaign order) {
        getUI().getNavigator().navigateTo(CampaignEditView.VIEW_NAME + "/" + order.getId());
    }

    private void newOrder() {
        getUI().getNavigator().navigateTo(CampaignEditView.VIEW_NAME + "/" + kindStrId);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        kindStrId = event.getParameters();
        list.setDataProvider(ofCollection(repo.findByContentKind(kindStrId)));
        addComponents(kindsLayout(), searchPanel(), list);
    }

    @NotNull
    private HorizontalLayout kindsLayout() {
        HorizontalLayout layout = new MHorizontalLayout();
        kindRepo.findAll().forEach(
                kind -> layout.addComponent(
                        new MButton(
                                kind.getName(),
                                click -> getUI().getNavigator().navigateTo(VIEW_NAME + "/" + kind.getStrId())
                        ).withStyleName(kind.getStrId().equals(kindStrId) ? ValoTheme.BUTTON_PRIMARY : "")
                )
        );
        return layout;
    }

    private Panel searchPanel() {
        Panel panel = new Panel();
        panel.setContent(newOrder);
        return panel;
    }
}