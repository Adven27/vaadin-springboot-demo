package com.sberbank.cms.ui.sidebar.planning;

import com.sberbank.cms.ui.sidebar.Sections;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import org.vaadin.spring.sidebar.annotation.SideBarItem;
import org.vaadin.spring.sidebar.annotation.VaadinFontIcon;

@SpringView(name = TestView.VIEW_NAME)
@SideBarItem(sectionId = Sections.PLANNING, caption = "Another View", order = 2)
@VaadinFontIcon(VaadinIcons.TABLE)
@ViewScope
public class TestView extends VerticalLayout implements View {

    private static final long serialVersionUID = -5940176536863140421L;
    
    public static final String VIEW_NAME = "test";

    public TestView() {
        addComponent(new Label("Planning View 2"));
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }
}