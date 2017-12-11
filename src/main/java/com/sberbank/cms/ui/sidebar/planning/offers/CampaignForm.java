package com.sberbank.cms.ui.sidebar.planning.offers;

import com.sberbank.cms.backend.content.Campaign;
import com.sberbank.cms.backend.content.CampaignRepository;
import com.sberbank.cms.backend.content.ContentKind;
import com.sberbank.cms.backend.content.ContentKindRepository;
import com.sberbank.cms.ui.common.forms.CommonForm;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.VerticalLayout;
import org.vaadin.spring.events.EventBus;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

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
        campaignRepo.save(ent);
    }

    @Override
    public FormLayout formLayout() {
        return new MFormLayout(kindComboBox(), fieldsContainer);
    }

    private void refreshFieldsFor(ContentKind kind) {
        fieldsContainer.removeAllComponents();
        if (kind != null) {
            fieldsContainer.addComponents(
                    kind.getFields().stream().
                            map(field -> field.getType().ui(field.getName(), getBinder())).
                            toArray(AbstractField[]::new)
            );
        }
    }

    private ComboBox<ContentKind> kindComboBox() {
        ComboBox<ContentKind> cb = new ComboBox<>("Kind", kindRepo.findAll());
        cb.setEmptySelectionAllowed(true);
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