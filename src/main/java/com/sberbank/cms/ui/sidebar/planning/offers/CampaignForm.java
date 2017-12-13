package com.sberbank.cms.ui.sidebar.planning.offers;

import com.sberbank.cms.backend.content.*;
import com.sberbank.cms.ui.common.forms.CommonForm;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.VerticalLayout;
import org.vaadin.spring.events.EventBus;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class CampaignForm extends CommonForm<Campaign> {
    private static final long serialVersionUID = 1L;

    private final CampaignRepository campaignRepo;
    private final ContentKindRepository kindRepo;
    private final VerticalLayout fieldsContainer = new MVerticalLayout();

    public CampaignForm(CampaignRepository campaignRepo, ContentKindRepository kindRepo, EventBus.UIEventBus b) {
        super(b, Campaign.class);
        this.campaignRepo = campaignRepo;
        this.kindRepo = kindRepo;
    }

    @Override
    public void save(Campaign ent) {
        List<String> kindFields = ent.getContentKind().getFields().stream().map(ContentField::getName).collect(toList());
        ent.setData(
                ent.getData().entrySet().stream().
                        filter(entry -> kindFields.contains(entry.getKey())).
                        collect(toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
        campaignRepo.save(ent);
    }

    @Override
    public FormLayout formLayout() {
        return new MFormLayout(kindComboBox(), fieldsContainer);
    }

    private void refreshFieldsFor(ContentKind kind) {
        //FIXME bindings are cleared only after form closing
        fieldsContainer.removeAllComponents();
        if (kind != null) {
            fieldsContainer.addComponents(
                    kind.getFields().stream().
                            map(field -> field.getType().ui(field.getName(), getBinder())).
                            toArray(AbstractField[]::new)
            );
        }
        if (getPopup() != null) {
            getPopup().center();
        }
    }

    private ComboBox<ContentKind> kindComboBox() {
        ComboBox<ContentKind> cb = new ComboBox<>("Kind", kindRepo.findAll());
        cb.setEmptySelectionAllowed(false);
        cb.addValueChangeListener(event -> refreshFieldsFor(event.getValue()));
        cb.setItemCaptionGenerator(ContentKind::getStrId);
        cb.focus();
        getBinder().bind(cb, Campaign::getContentKind, Campaign::setContentKind);
        return cb;
    }

    @Override
    protected void bind() {
//        super.bind();
    }
}