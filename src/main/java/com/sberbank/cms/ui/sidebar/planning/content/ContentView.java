package com.sberbank.cms.ui.sidebar.planning.content;

import com.sberbank.cms.backend.content.ContentKind;
import com.sberbank.cms.backend.content.ContentKindRepository;
import com.sberbank.cms.ui.sidebar.Sections;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
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
@SpringView(name = ContentView.VIEW_NAME)
@SideBarItem(sectionId = Sections.PLANNING, caption = "Content", order = 1)
@VaadinFontIcon(WRENCH)
@ViewScope
public class ContentView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "";//DEFAULT VIEW
    private static final long serialVersionUID = 1L;

    private final ContentKindRepository repo;
    private final ContentForm contentForm;
    private final EventBus.UIEventBus eventBus;

    private MGrid<ContentKind> list = new MGrid<>(ContentKind.class)
            .withProperties("strId", "name", "creationDate")
            .withColumnHeaders("StrId", "Name", "Created")
            .withFullSize();

    private MTextField filterByName = new MTextField().withPlaceholder("Filter by name");
    private Button addNew = new MButton(PLUS, this::add);


    public ContentView(ContentKindRepository repo, ContentForm contentForm, EventBus.UIEventBus eventBus) {
        this.repo = repo;
        this.contentForm = contentForm;
        this.eventBus = eventBus;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        list.setSelectionMode(NONE);
        list.addComponentColumn(kind -> new HorizontalLayout(
                        new MButton(PENCIL, click -> edit(kind)),
                        new ConfirmButton(TRASH, "Are you sure you want to delete the entry?", () -> {
                            repo.delete(kind);
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
        list.setRows(repo.findByNameLikeIgnoreCase(likeFilter));
    }

    private void add(Button.ClickEvent clickEvent) {
        edit(new ContentKind());
    }

    private void edit(final ContentKind kind) {
        contentForm.setEntity(kind);
        contentForm.openInModalPopup();
    }

    @EventBusListenerMethod(scope = EventScope.UI)
    public void onModified(ContentKind event) {
        listEntities();
        contentForm.closePopup();
    }
}