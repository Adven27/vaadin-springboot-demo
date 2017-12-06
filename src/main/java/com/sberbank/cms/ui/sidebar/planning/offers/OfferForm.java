package com.sberbank.cms.ui.sidebar.planning.offers;

import com.sberbank.cms.backend.Offer;
import com.sberbank.cms.backend.OfferRepository;
import com.sberbank.cms.ui.common.forms.CommonForm;
import com.vaadin.data.converter.LocalDateToDateConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import org.vaadin.spring.events.EventBus;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.layouts.MFormLayout;

@UIScope
@SpringComponent
public class OfferForm extends CommonForm<Offer> {
    private static final long serialVersionUID = 1L;

    private OfferRepository repo;
    private TextField name = new MTextField("Name");
    private RichTextArea desc = new RichTextArea("Desc");
    private TextField weight = new MTextField("Weight");
    private DateField expirationDate = new DateField("Expiration date");
    private CheckBox flag = new CheckBox("Flag");

    public OfferForm(OfferRepository r, EventBus.UIEventBus b) {
        super(b, Offer.class);
        repo = r;
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
    public void save(Offer ent) {
        repo.save(ent);
    }

    @Override
    public FormLayout formLayout() {
        return new MFormLayout(name, desc, weight, expirationDate, flag);
    }
}