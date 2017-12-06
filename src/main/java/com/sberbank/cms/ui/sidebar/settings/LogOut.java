package com.sberbank.cms.ui.sidebar.settings;

import com.sberbank.cms.ui.sidebar.Sections;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vaadin.spring.security.VaadinSecurity;
import org.vaadin.spring.sidebar.annotation.SideBarItem;
import org.vaadin.spring.sidebar.annotation.VaadinFontIcon;

import java.io.Serializable;

@SideBarItem(sectionId = Sections.SETTINGS, caption = "Log out")
@VaadinFontIcon(VaadinIcons.OUT)
@Component
@UIScope
public class LogOut implements Runnable, Serializable {
    private static final long serialVersionUID = 7376470664287797415L;
    private final VaadinSecurity vaadinSecurity;

    @Autowired
    public LogOut(VaadinSecurity vaadinSecurity) {
        this.vaadinSecurity = vaadinSecurity;
    }

    @Override
    public void run() {
        vaadinSecurity.logout();
    }
}