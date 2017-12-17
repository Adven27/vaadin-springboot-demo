package com.sberbank.cms.ui.sidebar.planning.offers;

import com.sberbank.cms.backend.content.*;
import com.sberbank.cms.ui.common.forms.CommonForm;
import com.vaadin.server.Setter;
import com.vaadin.ui.*;
import org.vaadin.spring.events.EventBus;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.vaadin.data.provider.DataProvider.ofCollection;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class CampaignForm extends CommonForm<Campaign> {
    private static final long serialVersionUID = 1L;

    private final CampaignRepository campaignRepo;
    private final ContentKindRepository kindRepo;
    private final PlaceRepository placeRepo;
    private final VerticalLayout fieldsContainer = new MVerticalLayout();
    private final ListSelect<String> places = new ListSelect<>();

    public CampaignForm(CampaignRepository campaignRepo, ContentKindRepository kindRepo, EventBus.UIEventBus b, PlaceRepository placeRepo) {
        super(b, Campaign.class);
        this.campaignRepo = campaignRepo;
        this.kindRepo = kindRepo;
        this.placeRepo = placeRepo;
    }

    @Override
    public void save(Campaign ent) {
        List<String> kindFields = ent.getContentKind().getFields().stream().map(ContentField::getName).collect(toList());
        ent.setData(
                ent.getData().entrySet().stream().
                        filter(entry -> kindFields.contains(entry.getKey()) || places.getCaption().equalsIgnoreCase(entry.getKey())).
                        collect(toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
        campaignRepo.save(ent);
    }

    @Override
    public FormLayout formLayout() {
        configurePlaces();
        return new MFormLayout(kindComboBox(), places, fieldsContainer);
    }

    private void configurePlaces() {
        places.setCaption("places");
        places.setRows(3);
        places.setDataProvider(ofCollection(placeRepo.findAllNames()));
        getBinder().forField(places).
                bind(campaign -> new HashSet<>((List<String>) campaign.getData().get(places.getCaption())), setter(places));
    }


    private Setter<Campaign, Set<String>> setter(Component component) {
        return (campaign, val) -> {
            Map<String, Object> data = campaign.getData();
            data.put(component.getCaption(), val);
            campaign.setData(data);
        };
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
        //FIXME manual binding... maybe need custom component for campaign.data field
        //super.bind();
    }

}