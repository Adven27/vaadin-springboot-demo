package com.sberbank.cms.ui.common.forms;

import com.vaadin.ui.Component;
import com.vaadin.ui.Layout;
import org.vaadin.spring.events.EventBus;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.layouts.MVerticalLayout;

public abstract class CommonForm<T> extends AbstractForm<T> {
    private static final long serialVersionUID = 1L;

    private final EventBus.UIEventBus eventBus;

    public CommonForm(EventBus.UIEventBus b, Class<T> aClass) {
        super(aClass);
        this.eventBus = b;

        setSavedHandler(ent -> {
            save(ent);
            eventBus.publish(this, ent);
        });
        setResetHandler(ent -> eventBus.publish(this, ent));
        setSizeUndefined();
    }

    @Override
    protected Component createContent() {
        return new MVerticalLayout(
                formLayout(),
                getToolbar()
        );
    }

    public abstract void save(T ent);

    public abstract Layout formLayout();
}