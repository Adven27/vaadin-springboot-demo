package com.sberbank.cms.ui.sidebar.views;

import com.sberbank.cms.backend.Offer;
import com.sberbank.cms.backend.OfferRepository;
import com.sberbank.cms.ui.offers.OfferForm;
import com.sberbank.cms.ui.offers.OfferModifiedEvent;
import com.sberbank.cms.ui.sidebar.Sections;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

@SpringView(name = OffersView.VIEW_NAME)
@SideBarItem(sectionId = Sections.PLANNING, caption = "Offers", order = 1)
@VaadinFontIcon(VaadinIcons.SHOP)
@ViewScope
public class OffersView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "";//default view
    private static final long serialVersionUID = 2217814051618370412L;

    @Autowired
    OfferRepository repo;
    @Autowired
    OfferForm offerForm;
    @Autowired
    EventBus.UIEventBus eventBus;

    private MGrid<Offer> list = new MGrid<>(Offer.class)
            .withProperties("id", "name", "desc", "weight", "expirationDate", "flag")
            .withColumnHeaders("id", "Name", "Desc", "Weight", "Expire", "Flag")
            // not yet supported by V8
            //.setSortableProperties("name", "email")
            .withFullWidth();

    private MTextField filterByName = new MTextField().withPlaceholder("Filter by name");
    private Button addNew = new MButton(VaadinIcons.PLUS, this::add);
    private Button edit = new MButton(VaadinIcons.PENCIL, this::edit);
    private Button delete = new ConfirmButton(VaadinIcons.TRASH, "Are you sure you want to delete the entry?", this::remove);

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        addComponent(
                new MVerticalLayout(
                        new MHorizontalLayout(filterByName, addNew, edit, delete)
                ).expand(
                        list
                )
        );
        listEntities();

        list.asSingleSelect().addValueChangeListener(e -> adjustActionButtonState());
        filterByName.addValueChangeListener(e -> listEntities(e.getValue()));

        eventBus.subscribe(this);
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
        //list.setRows(repo.findByNameLikeIgnoreCase(likeFilter));

        list.setDataProvider(
                (sortOrder, offset, limit) -> {
                    final List<Offer> page = repo.findByNameLikeIgnoreCase(likeFilter,
                            new PageRequest(
                                    offset / limit,
                                    limit,
                                    sortOrder.isEmpty() || sortOrder.get(0).getDirection() == SortDirection.ASCENDING
                                            ? Sort.Direction.ASC : Sort.Direction.DESC,
                                    sortOrder.isEmpty() ? "id" : sortOrder.get(0).getSorted()
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