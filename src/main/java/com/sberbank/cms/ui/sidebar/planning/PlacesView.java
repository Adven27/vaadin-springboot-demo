package com.sberbank.cms.ui.sidebar.planning;

import com.sberbank.cms.backend.content.Place;
import com.sberbank.cms.backend.content.PlaceRepository;
import com.sberbank.cms.ui.common.forms.AddButton;
import com.sberbank.cms.ui.sidebar.Sections;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.UserError;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import org.vaadin.spring.sidebar.annotation.SideBarItem;
import org.vaadin.spring.sidebar.annotation.VaadinFontIcon;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import static com.vaadin.icons.VaadinIcons.TAGS;
import static com.vaadin.icons.VaadinIcons.TRASH;
import static com.vaadin.ui.Alignment.BOTTOM_CENTER;
import static com.vaadin.ui.Grid.SelectionMode.NONE;

@SpringView(name = PlacesView.VIEW_NAME)
@SideBarItem(sectionId = Sections.PLANNING, caption = "Places", order = 2)
@VaadinFontIcon(TAGS)
@ViewScope
public class PlacesView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "places";

    private final PlaceRepository repo;
    private final Grid<Place> grid = new MGrid<>(Place.class).withProperties("name").withFullSize();
    private TextField name;

    public PlacesView(PlaceRepository repo) {
        this.repo = repo;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        MButton add = new AddButton(click -> add());
        add.setEnabled(false);

        name = new MTextField("", input -> add.setEnabled(input.getValue().length() > 0)).withPlaceholder("Place...");
        configureGrid();

        addComponent(
                new VerticalLayout(
                        new MHorizontalLayout(name, add).expand(name).alignAll(BOTTOM_CENTER),
                        grid
                )
        );
        refreshGrid();
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.setSelectionMode(NONE);
        grid.addComponentColumn(place -> new MButton(TRASH, click -> del(place)));
        grid.getColumn("name").setEditorComponent(new TextField());
        grid.getEditor().setEnabled(true).setBuffered(true).addSaveListener(e -> saveAndRefreshGrid(e.getBean()));
    }

    private void saveAndRefreshGrid(Place bean) {
        repo.save(bean);
        refreshGrid();
    }

    private void del(Place place) {
        repo.delete(place);
        refreshGrid();
    }

    private void add() {
        Place place = new Place();
        place.setName(name.getValue());
        try {
            saveAndRefreshGrid(place);
            name.clear();
        } catch (Exception e) {
            name.setComponentError(new UserError(e.getMessage()));
        }
    }

    private void refreshGrid() {
        grid.getEditor().cancel();
        grid.setItems(repo.findAll());
    }
}