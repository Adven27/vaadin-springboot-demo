package com.sberbank.cms.ui.sidebar.settings;

import com.sberbank.cms.ui.sidebar.Sections;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.UI;
import org.springframework.stereotype.Component;
import org.vaadin.spring.sidebar.annotation.SideBarItem;
import org.vaadin.spring.sidebar.annotation.VaadinFontIcon;

import java.io.Serializable;

@SideBarItem(sectionId = Sections.SETTINGS, caption = "Log out")
@VaadinFontIcon(VaadinIcons.OUT)
@Component
@UIScope
public class LogOut implements Runnable, Serializable {

    @Override
    public void run() {
        UI current = UI.getCurrent();
        current.getSession().getSession().invalidate();
        current.getPage().reload();
    }
}