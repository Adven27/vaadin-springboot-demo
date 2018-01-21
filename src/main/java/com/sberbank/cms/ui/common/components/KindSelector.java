package com.sberbank.cms.ui.common.components;

import com.sberbank.cms.backend.domain.services.ContentKindRepository;
import com.sberbank.cms.ui.sidebar.planning.campaigns.CampaignsView;
import com.vaadin.ui.CssLayout;
import org.vaadin.viritin.button.MButton;

import static com.vaadin.ui.themes.ValoTheme.BUTTON_PRIMARY;
import static com.vaadin.ui.themes.ValoTheme.LAYOUT_COMPONENT_GROUP;

public class KindSelector extends CssLayout {
    private final String selected;

    public KindSelector(ContentKindRepository repo, String selected) {
        this.selected = selected;

        setStyleName(LAYOUT_COMPONENT_GROUP);
        repo.findAll().forEach(
                kind -> addComponent(
                        new MButton(
                                kind.getName(),
                                click -> getUI().getNavigator().navigateTo(CampaignsView.VIEW_NAME + "/" + kind.getStrId())
                        ).withStyleName(kind.getStrId().equals(this.selected) ? BUTTON_PRIMARY : "")
                )
        );
    }

    public String selected() {
        return selected;
    }
}