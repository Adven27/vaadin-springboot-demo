package com.sberbank.cms.ui.common.components;

import com.vaadin.icons.VaadinIcons;
import org.vaadin.viritin.button.MButton;

public class EditButton extends MButton {
    public EditButton(ClickListener listener) {
        super(VaadinIcons.PENCIL, listener);
    }
}