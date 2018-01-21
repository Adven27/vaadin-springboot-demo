package com.sberbank.cms.ui.sidebar.settings.users;

import com.sberbank.cms.backend.security.Role;
import com.sberbank.cms.backend.security.UserInfo;
import com.sberbank.cms.backend.security.UserRepository;
import com.sberbank.cms.ui.common.components.AddButton;
import com.sberbank.cms.ui.common.components.EditButton;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import org.springframework.security.access.annotation.Secured;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.spring.sidebar.annotation.SideBarItem;
import org.vaadin.spring.sidebar.annotation.VaadinFontIcon;
import org.vaadin.viritin.button.ConfirmButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import static com.sberbank.cms.ui.sidebar.Sections.SETTINGS;
import static com.vaadin.icons.VaadinIcons.USERS;

@Secured({Role.ROLE_ADMIN})
@SpringView(name = UsersView.VIEW_NAME)
@SideBarItem(sectionId = SETTINGS, caption = "Users", order = 1)
@VaadinFontIcon(USERS)
public class UsersView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "users";

    private final UserRepository repo;
    private final UserForm userForm;
    private final EventBus.UIEventBus eventBus;

    private MGrid<UserInfo> list = new MGrid<>(UserInfo.class)
            .withProperties("id", "name", "login", "role")
            .withColumnHeaders("id", "Name", "Login", "Role")
            .withFullSize();

    private MTextField filter = new MTextField().withPlaceholder("Search by name or login...");
    private Button add = new AddButton(this::add);
    private Button edit = new EditButton(this::edit);
    private Button delete = new ConfirmButton(VaadinIcons.TRASH, "Are you sure you want to delete the entry?", this::remove);

    public UsersView(UserRepository repo, UserForm userForm, EventBus.UIEventBus eventBus) {
        this.repo = repo;
        this.userForm = userForm;
        this.eventBus = eventBus;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        addComponent(
                new MVerticalLayout(
                        new MHorizontalLayout(filter, add, edit, delete).expand(filter)
                ).expand(
                        new MHorizontalLayout().expand(list)
                )
        );
        listEntities();

        list.asSingleSelect().addValueChangeListener(e -> adjustActionButtonState());
        filter.addValueChangeListener(e -> listEntities(e.getValue()));

        eventBus.subscribe(this);
    }

    private void adjustActionButtonState() {
        boolean hasSelection = !list.getSelectedItems().isEmpty();
        edit.setEnabled(hasSelection);
        delete.setEnabled(hasSelection);
    }

    private void listEntities() {
        listEntities(filter.getValue());
    }

    private void listEntities(String nameFilter) {
        String likeFilter = "%" + nameFilter + "%";
        list.setRows(repo.findByLoginLikeIgnoreCaseOrNameLikeIgnoreCase(likeFilter, likeFilter));
        adjustActionButtonState();
    }

    private void add(Button.ClickEvent e) {
        edit(new UserInfo());
    }

    private void edit(Button.ClickEvent e) {
        edit(list.asSingleSelect().getValue());
    }

    private void edit(final UserInfo userInfo) {
        userForm.setEntity(userInfo);
        userForm.openInModalPopup();
    }

    private void remove() {
        repo.delete(list.asSingleSelect().getValue());
        list.deselectAll();
        listEntities();
    }

    @EventBusListenerMethod(scope = EventScope.UI)
    public void onModified(UserInfo event) {
        listEntities();
        userForm.closePopup();
    }
}