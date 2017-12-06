package com.sberbank.cms.ui.sidebar.planning.offers;

import com.sberbank.cms.backend.Offer;
import com.sberbank.cms.backend.OfferRepository;
import com.sberbank.cms.ui.sidebar.Sections;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
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

import java.util.List;

import static com.sberbank.cms.backend.Role.ROLE_ADMIN;
import static com.sberbank.cms.backend.Role.ROLE_OFFICER;
import static com.vaadin.icons.VaadinIcons.SHOP;

@Secured({ROLE_ADMIN, ROLE_OFFICER})
@SpringView(name = OffersView.VIEW_NAME)
@SideBarItem(sectionId = Sections.PLANNING, caption = "Offers", order = 1)
@VaadinFontIcon(SHOP)
@ViewScope
public class OffersView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "";//default view
    private static final long serialVersionUID = 2217814051618370412L;

    private final OfferRepository repo;
    private final OfferForm offerForm;
    private final EventBus.UIEventBus eventBus;

    private MGrid<Offer> list = new MGrid<>(Offer.class)
            .withProperties("id", "name", "desc", "weight", "expirationDate", "flag")
            .withColumnHeaders("id", "Name", "Desc", "Weight", "Expire", "Flag")
            .withFullSize();

    private MTextField filterByName = new MTextField().withPlaceholder("Filter by name");
    private Button addNew = new MButton(VaadinIcons.PLUS, this::add);
    private Button edit = new MButton(VaadinIcons.PENCIL, this::edit);
    private Button delete = new ConfirmButton(VaadinIcons.TRASH, "Are you sure you want to delete the entry?", this::remove);

    public OffersView(OfferRepository repo, OfferForm offerForm, EventBus.UIEventBus eventBus) {
        this.repo = repo;
        this.offerForm = offerForm;
        this.eventBus = eventBus;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        addComponent(
                new MVerticalLayout(
                        new MHorizontalLayout(filterByName, addNew, edit, delete).expand(filterByName)
                ).expand(
                        new MHorizontalLayout().expand(list)
                )
        );
        listEntities();

        list.asSingleSelect().addValueChangeListener(e -> adjustActionButtonState());
        filterByName.addValueChangeListener(e -> listEntities(e.getValue()));

        eventBus.subscribe(this);
        setSizeFull();
    }

    private void adjustActionButtonState() {
        boolean hasSelection = !list.getSelectedItems().isEmpty();
        edit.setEnabled(hasSelection);
        delete.setEnabled(hasSelection);
    }

    private void listEntities() {
        listEntities(filterByName.getValue());
    }

    private void listEntities(String nameFilter) {
        String likeFilter = "%" + nameFilter + "%";

        list.setDataProvider(
                (sort, offset, limit) -> {
                    final List<Offer> page = repo.findByNameLikeIgnoreCase(likeFilter,
                            new PageRequest(
                                    offset / limit,
                                    limit,
                                    sort.isEmpty() || sort.get(0).getDirection() == SortDirection.ASCENDING
                                            ? Sort.Direction.ASC : Sort.Direction.DESC,
                                    sort.isEmpty() ? "id" : sort.get(0).getSorted()
                            )
                    );
                    return page.subList(offset % limit, page.size()).stream();
                },
                () -> (int) repo.countByNameLike(likeFilter)
        );
        adjustActionButtonState();
    }

    private void add(Button.ClickEvent clickEvent) {
        edit(new Offer());
    }

    private void edit(Button.ClickEvent e) {
        edit(list.asSingleSelect().getValue());
    }

    private void edit(final Offer phoneBookEntry) {
        offerForm.setEntity(phoneBookEntry);
        offerForm.openInModalPopup();
    }

    private void remove() {
        repo.delete(list.asSingleSelect().getValue());
        list.deselectAll();
        listEntities();
    }

    @EventBusListenerMethod(scope = EventScope.UI)
    public void onPersonModified(OfferModifiedEvent event) {
        listEntities();
        offerForm.closePopup();
    }
}