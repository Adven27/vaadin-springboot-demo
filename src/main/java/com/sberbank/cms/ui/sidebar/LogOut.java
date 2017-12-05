package com.sberbank.cms.ui.sidebar;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.UI;
import org.springframework.stereotype.Component;
import org.vaadin.spring.sidebar.annotation.SideBarItem;
import org.vaadin.spring.sidebar.annotation.VaadinFontIcon;

import java.io.Serializable;

@SideBarItem(sectionId = Sections.EXECUTION, caption = "Log out")
@VaadinFontIcon(VaadinIcons.OUT)
@Component
@UIScope
public class LogOut implements Runnable, Serializable {
    private static final long serialVersionUID = 7376470664287797415L;

    @Override
    public void run() {
        UI ui = UI.getCurrent();
        ui.getSession().getSession().invalidate();
        ui.getPage().reload();
    }
}