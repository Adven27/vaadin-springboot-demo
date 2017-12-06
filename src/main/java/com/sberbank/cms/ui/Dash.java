package com.sberbank.cms.ui;

import com.sberbank.cms.ui.sidebar.AccessDeniedView;
import com.sberbank.cms.ui.sidebar.ErrorView;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.spring.security.VaadinSecurity;
import org.vaadin.spring.security.util.SecurityExceptionUtils;
import org.vaadin.spring.sidebar.components.ValoSideBar;
import org.vaadin.spring.sidebar.security.VaadinSecurityItemFilter;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import static com.vaadin.icons.VaadinIcons.ROCKET;
import static com.vaadin.shared.ui.ContentMode.HTML;

@SpringUI
@Theme(ValoTheme.THEME_NAME)
public class Dash extends UI {
    private static final long serialVersionUID = -7747249047198990160L;

    private final SpringViewProvider viewProvider;
    private final ValoSideBar sideBar;
    private final VaadinSecurity vaadinSecurity;

    public Dash(SpringViewProvider viewProvider, ValoSideBar sideBar, VaadinSecurity vaadinSecurity) {
        this.viewProvider = viewProvider;
        this.sideBar = sideBar;
        this.vaadinSecurity = vaadinSecurity;
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        getPage().setTitle("Dashboard");
        setErrorHandler(new DefaultErrorHandler() {
            @Override
            public void error(com.vaadin.server.ErrorEvent event) {
                if (SecurityExceptionUtils.isAccessDeniedException(event.getThrowable())) {
                    Notification.show("Sorry, you don't have access to do that.");
                } else {
                    super.error(event);
                }
            }
        });

        final MCssLayout viewContainer = new MCssLayout().withFullSize();
        setNavigator(navigator(viewContainer));

        configureSideBar();

        // Call this here because the Navigator must have been configured before the Side Bar can be attached to a UI.
        setContent(new MHorizontalLayout().withFullSize().add(sideBar).expand(viewContainer));
    }

    private void configureSideBar() {
        sideBar.setItemFilter(new VaadinSecurityItemFilter(vaadinSecurity));
        sideBar.setLargeIcons(true);
        sideBar.setLogo(new Label(ROCKET.getHtml(), HTML));
    }

    private Navigator navigator(Layout viewContainer) {
        // Without an AccessDeniedView, the view provider would act like the restricted views did not exist at all.
        viewProvider.setAccessDeniedViewClass(AccessDeniedView.class);

        final Navigator nav = new Navigator(this, viewContainer);
        nav.setErrorView(ErrorView.class);
        nav.addProvider(viewProvider);
        return nav;
    }
}