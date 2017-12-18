package com.sberbank.cms.ui.common.forms;

import com.vaadin.icons.VaadinIcons;
import org.vaadin.viritin.button.MButton;

import static com.vaadin.event.ShortcutAction.KeyCode.ENTER;
import static com.vaadin.ui.themes.ValoTheme.BUTTON_PRIMARY;

public class AddButton extends MButton {
    public AddButton(ClickListener listener) {
        super(VaadinIcons.PLUS, listener);
        withStyleName(BUTTON_PRIMARY);
        withClickShortcut(ENTER);
    }
}