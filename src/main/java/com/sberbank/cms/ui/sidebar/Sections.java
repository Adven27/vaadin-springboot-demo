package com.sberbank.cms.ui.sidebar;

import org.springframework.stereotype.Component;
import org.vaadin.spring.sidebar.annotation.SideBarSection;
import org.vaadin.spring.sidebar.annotation.SideBarSections;

/**
 * This is a Spring-managed bean that does not do anything. Its only purpose is to define
 * the sections of the side bar. The reason it is configured as a bean is that it makes it possible
 * to lookup the annotations from the Spring application context.
 */
@SideBarSections({
        @SideBarSection(id = Sections.PLANNING, caption = "Section 1"),
        @SideBarSection(id = Sections.EXECUTION, caption = "Section 2"),
})
@Component
public class Sections {
    public static final String PLANNING = "planning";
    public static final String EXECUTION = "execution";
}