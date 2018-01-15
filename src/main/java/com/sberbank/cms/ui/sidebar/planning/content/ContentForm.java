package com.sberbank.cms.ui.sidebar.planning.content;

import com.sberbank.cms.backend.content.ContentField;
import com.sberbank.cms.backend.content.ContentKind;
import com.sberbank.cms.backend.content.ContentKindRepository;
import com.sberbank.cms.backend.content.FieldType;
import com.sberbank.cms.ui.common.forms.CommonForm;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import org.vaadin.spring.events.EventBus;
import org.vaadin.viritin.fields.ElementCollectionField;
import org.vaadin.viritin.fields.EnumSelect;
import org.vaadin.viritin.fields.IntegerField;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.layouts.MFormLayout;

import java.util.List;

@ViewScope
@SpringComponent
public class ContentForm extends CommonForm<ContentKind> {
    private static final long serialVersionUID = 1L;

    private ContentKindRepository repo;

    private TextField strId = new MTextField("StrId");
    private TextField name = new MTextField("Name");

    public static class FieldRow {
        TextField name = new MTextField().withPlaceholder("name");
        EnumSelect<FieldType> type = (EnumSelect) new EnumSelect(FieldType.class);
        IntegerField order = new IntegerField();
    }

    private ElementCollectionField<ContentField, List<ContentField>> fields =
            new ElementCollectionField<>(ContentField.class, FieldRow.class);

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
        fields.addElementAddedListener(e -> e.getElement().setKind(getEntity()));
        return new MFormLayout(strId, name, fields);
    }
}