package com.sberbank.cms.ui.sidebar.planning.content;

import com.sberbank.cms.backend.content.ContentKind;
import com.sberbank.cms.backend.content.ContentKindRepository;
import com.sberbank.cms.ui.common.forms.CommonForm;
import com.sberbank.cms.ui.common.forms.FieldList;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import org.vaadin.spring.events.EventBus;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.layouts.MFormLayout;

@ViewScope
@SpringComponent
public class ContentForm extends CommonForm<ContentKind> {
    private static final long serialVersionUID = 1L;

    private ContentKindRepository repo;

    private TextField strId = new MTextField("StrId");
    private TextField name = new MTextField("Name");
    private FieldList fields = new FieldList();

    public ContentForm(ContentKindRepository r, EventBus.UIEventBus b) {
        super(b, ContentKind.class);
        repo = r;
    }

    @Override
    public void save(ContentKind ent) {
        repo.save(ent);
    }

    @Override
    public FormLayout formLayout() {
        return new MFormLayout(strId, name, fields);
    }

    @Override
    public void attach() {
        super.attach();
        fields.owner(getEntity());
    }
}