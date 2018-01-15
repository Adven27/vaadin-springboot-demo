package com.sberbank.cms.ui.common.forms;

import com.vaadin.icons.VaadinIcons;
import org.vaadin.viritin.button.PrimaryButton;

public class AddButton extends PrimaryButton {
    public AddButton(ClickListener listener) {
        super(VaadinIcons.PLUS, listener);
    }
}