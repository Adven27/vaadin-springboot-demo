package com.sberbank.cms.ui.common.forms;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.viritin.button.ConfirmButton;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import static com.vaadin.icons.VaadinIcons.PENCIL;
import static com.vaadin.icons.VaadinIcons.TRASH;
import static com.vaadin.ui.Grid.SelectionMode.NONE;

public abstract class CustomGrid<T> extends CustomComponent {
    private Class<T> clazz;
    private AbstractForm form;

    private MTextField filterByName = new MTextField().withPlaceholder("Filter by name");

    private final Grid<T> list;

    public CustomGrid(EventBus.UIEventBus eventBus, Class<T> clazz) {
        this.clazz = clazz;
        list = grid();
        // A layout structure used for composition
        list.setSelectionMode(NONE);
        list.addComponentColumn(campaign -> new HorizontalLayout(
                        new MButton(PENCIL, click -> edit(campaign)),
                        new ConfirmButton(TRASH, "Are you sure you want to delete the entry?", () -> {
                            deleteRow(campaign);
                            listEntities();
                        })
                )
        );
        setCompositionRoot(
                new MVerticalLayout(
                        new MHorizontalLayout(filterByName, new MButton(VaadinIcons.PLUS, click -> edit(addRow()))).expand(filterByName)
                ).expand(
                        new MHorizontalLayout().expand(list)
                )
        );
        listEntities();

        filterByName.addValueChangeListener(e -> listEntities(list, e.getValue()));

        eventBus.subscribe(this);
        setSizeFull();
    }

    public abstract T addRow();

    public abstract MGrid<T> grid();

    public abstract void deleteRow(T row);

    public abstract void listEntities(Grid<T> grid, String nameFilter);

    private void listEntities() {
        listEntities(list, filterByName.getValue());
    }

    private void edit(final T row) {
        form = form();
        form.setEntity(row);
        form.openInModalPopup();
    }

    protected abstract AbstractForm form();

    @EventBusListenerMethod(scope = EventScope.UI)
    public void onModified(T event) {
        if (event.getClass().isAssignableFrom(clazz)) {
            listEntities();
            form.closePopup();
        }
    }
}