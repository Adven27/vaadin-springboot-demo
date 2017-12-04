package com.sberbank.cms.ui;

import com.sberbank.cms.ui.sidebar.views.ErrorView;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.sidebar.components.ValoSideBar;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

@SpringUI
@Theme(ValoTheme.THEME_NAME)
public class Dash extends UI {
    private static final long serialVersionUID = -7747249047198990160L;

    @Autowired
    SpringViewProvider viewProvider;
    @Autowired
    ValoSideBar sideBar;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        getPage().setTitle("Dashboard");

        final VerticalLayout viewContainer = new MVerticalLayout().withFullSize();
        setNavigator(navigator(viewContainer));
        setContent(new MHorizontalLayout().withFullSize().add(sideBar).expand(viewContainer));

        sideBar.setLargeIcons(true);
        sideBar.setLogo(new Label(FontAwesome.ROCKET.getHtml(), ContentMode.HTML));
    }

    private Navigator navigator(VerticalLayout viewContainer) {
        final Navigator nav = new Navigator(this, viewContainer);
        nav.setErrorView(ErrorView.class);
        nav.addProvider(viewProvider);
        return nav;
    }
}