package com.sberbank.cms.ui.sidebar.settings.users;

import com.sberbank.cms.backend.Role;
import com.sberbank.cms.backend.UserInfo;
import com.sberbank.cms.backend.UserRepository;
import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.vaadin.spring.events.EventBus;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

@UIScope
@SpringComponent
public class UserForm extends AbstractForm<UserInfo> {
    private static final long serialVersionUID = 1L;

    private EventBus.UIEventBus eventBus;
    private UserRepository repo;
    private PasswordEncoder passwordEncoder;
    private TextField name = new MTextField("Name");
    private TextField login = new MTextField("Login");
    private PasswordField password = new PasswordField("Password");
    private ComboBox<String> role = new ComboBox<>("Role");

    UserForm(UserRepository r, EventBus.UIEventBus b, PasswordEncoder passwordEncoder) {
        super(UserInfo.class);
        this.repo = r;
        this.eventBus = b;
        this.passwordEncoder = passwordEncoder;

        setSavedHandler(userInfo -> {
            repo.save(userInfo);
            eventBus.publish(this, new UserModifiedEvent(userInfo));
        });
        setResetHandler(userInfo -> eventBus.publish(this, new UserModifiedEvent(userInfo)));
        setSizeUndefined();
        role.setItems(Role.ALL);
    }

    @Override
    protected void bind() {
        getBinder().forMemberField(password).withNullRepresentation("").withConverter(passwordConverter());
        super.bind();
    }

    private Converter<String, String> passwordConverter() {
        return new Converter<String, String>() {
            @Override
            public Result<String> convertToModel(String value, ValueContext context) {
                return Result.ok(value == null ? "" : passwordEncoder.encode(value));
            }

            @Override
            public String convertToPresentation(String value, ValueContext context) {
                return value;
            }
        };
    }

    @Override
    protected Component createContent() {
        return new MVerticalLayout(
                new MFormLayout(
                        name,
                        login,
                        password,
                        role
                ),
                getToolbar()
        );
    }
}