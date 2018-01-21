package com.sberbank.cms.ui.sidebar.planning;

import com.sberbank.cms.ui.sidebar.Sections;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Notification;
import org.springframework.stereotype.Component;
import org.vaadin.spring.sidebar.annotation.SideBarItem;
import org.vaadin.spring.sidebar.annotation.VaadinFontIcon;

import java.io.Serializable;

/**
 * Just example of runnable menu item
 */
@SideBarItem(sectionId = Sections.CONTENT, caption = "Operation")
@VaadinFontIcon(VaadinIcons.REFRESH)
@Component
@UIScope
public class PlanningOperation implements Runnable, Serializable {
    private static final long serialVersionUID = -5503674215883178949L;

    @Override
    public void run() {
        Notification.show("Operation executed!");
    }
}