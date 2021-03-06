package com.sberbank.cms.ui.sidebar.planning.content;

import com.sberbank.cms.backend.domain.model.ContentKind;
import com.sberbank.cms.backend.domain.services.ContentKindRepository;
import com.sberbank.cms.ui.common.forms.CommonForm;
import com.sberbank.cms.ui.common.forms.CustomGrid;
import com.sberbank.cms.ui.sidebar.Sections;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import org.springframework.security.access.annotation.Secured;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.sidebar.annotation.SideBarItem;
import org.vaadin.spring.sidebar.annotation.VaadinFontIcon;
import org.vaadin.viritin.grid.MGrid;

import static com.sberbank.cms.backend.security.Role.ROLE_ADMIN;
import static com.sberbank.cms.backend.security.Role.ROLE_OFFICER;
import static com.vaadin.icons.VaadinIcons.WRENCH;

@Secured({ROLE_ADMIN, ROLE_OFFICER})
@SpringView(name = ContentView.VIEW_NAME)
@SideBarItem(sectionId = Sections.CONTENT, caption = "Content", order = 3)
@VaadinFontIcon(WRENCH)
@ViewScope
public class ContentView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "";//DEFAULT VIEW
    private static final long serialVersionUID = 1L;

    private final ContentKindRepository repo;
    private final ContentForm form;
    private final EventBus.UIEventBus eventBus;

    public ContentView(ContentKindRepository repo, ContentForm form, EventBus.UIEventBus eventBus) {
        this.repo = repo;
        this.form = form;
        this.eventBus = eventBus;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        addComponent(new CustomGrid<ContentKind>(eventBus, ContentKind.class) {
            @Override
            public ContentKind create() {
                return new ContentKind();
            }

            @Override
            public void delete(ContentKind row) {
                repo.delete(row);
            }

            @Override
            public MGrid<ContentKind> grid() {
                return new MGrid<>(ContentKind.class)
                        .withProperties("strId", "name", "creationDate")
                        .withColumnHeaders("StrId", "Name", "Created")
                        .withFullSize();
            }

            @Override
            public void listEntities(Grid<ContentKind> grid, String nameFilter) {
                grid.setItems(repo.findByNameLikeIgnoreCase("%" + nameFilter + "%"));

            }

            @Override
            protected CommonForm form() {
                return form;
            }
        });
    }
}