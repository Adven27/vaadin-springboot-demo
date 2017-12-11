package com.sberbank.cms.ui.sidebar.settings.users;

import com.sberbank.cms.security.Role;
import com.sberbank.cms.security.UserInfo;
import com.sberbank.cms.security.UserRepository;
import com.sberbank.cms.ui.common.forms.CommonForm;
import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.vaadin.spring.events.EventBus;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.layouts.MFormLayout;

import static java.util.Arrays.asList;

@UIScope
@SpringComponent
public class UserForm extends CommonForm<UserInfo> {
    private static final long serialVersionUID = 1L;

    private UserRepository repo;
    private PasswordEncoder passwordEncoder;
    private TextField name = new MTextField("Name");
    private TextField login = new MTextField("Login");
    private PasswordField password = new PasswordField("Password");
    private ComboBox<String> role = new ComboBox<>("Role", asList(Role.ALL));

    UserForm(UserRepository r, EventBus.UIEventBus b, PasswordEncoder passwordEncoder) {
        super(b, UserInfo.class);
        this.repo = r;
        this.passwordEncoder = passwordEncoder;
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
    public void save(UserInfo ent) {
        repo.save(ent);
    }

    @Override
    public FormLayout formLayout() {
        return new MFormLayout(name, login, password, role);
    }
}