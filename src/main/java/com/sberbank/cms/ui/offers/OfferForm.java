package com.sberbank.cms.ui.offers;

import com.sberbank.cms.backend.Offer;
import com.sberbank.cms.backend.OfferRepository;
import com.vaadin.data.converter.LocalDateToDateConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import org.vaadin.spring.events.EventBus;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

@UIScope
@SpringComponent
public class OfferForm extends AbstractForm<Offer> {
    private static final long serialVersionUID = 1L;

    EventBus.UIEventBus eventBus;
    OfferRepository repo;

    TextField name = new MTextField("Name");
    RichTextArea desc = new RichTextArea("Desc");
    TextField weight = new MTextField("Weight");
    DateField expirationDate = new DateField("Expiration date");
    CheckBox flag = new CheckBox("Flag");

    OfferForm(OfferRepository r, EventBus.UIEventBus b) {
        super(Offer.class);
        this.repo = r;
        this.eventBus = b;

        setSavedHandler(offer -> {
            repo.save(offer);
            eventBus.publish(this, new OfferModifiedEvent(offer));
        });
        setResetHandler(offer -> eventBus.publish(this, new OfferModifiedEvent(offer)));
        setSizeUndefined();
    }

    @Override
    protected void bind() {
        getBinder().forMemberField(expirationDate).
                withConverter(new LocalDateToDateConverter());
        getBinder().forMemberField(weight).
                withNullRepresentation("").withConverter(new StringToIntegerConverter(0, "integers only"));
        super.bind();
    }

    @Override
    protected Component createContent() {
        return new MVerticalLayout(
                new MFormLayout(
                        name,
                        desc,
                        weight,
                        expirationDate,
                        flag
                ),
                getToolbar()
        );
    }
}