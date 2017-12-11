package com.sberbank.cms.ui.common.forms;

import com.sberbank.cms.backend.content.ContentField;
import com.sberbank.cms.backend.content.ContentKind;
import com.sberbank.cms.backend.content.FieldType;
import com.vaadin.data.Binder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.*;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.util.ArrayList;
import java.util.List;

import static com.vaadin.icons.VaadinIcons.TRASH;
import static com.vaadin.ui.Grid.SelectionMode.NONE;
import static java.util.Arrays.asList;

public class FieldList extends CustomField<List<ContentField>> {
    private List<ContentField> value = new ArrayList<>();
    private ContentKind owner;
    private Button addNew = new MButton(VaadinIcons.PLUS, this::addField);
    private MGrid<ContentField> fields = new MGrid<>(ContentField.class).withProperties("name", "type");

    @Override
    protected Component initContent() {
        fields.setSelectionMode(NONE);
        fields.getEditor().setEnabled(true).setBuffered(true);//.setErrorGenerator((fieldToColumn, status) -> "!!!!!ERRROR\n" + fieldToColumn + status);
        fields.addComponentColumn(field -> new MButton(TRASH, click -> {
            value.remove(field);
            updateFields();
        }));


        fields.getEditor().addSaveListener(event -> updateFields());
        Binder<ContentField> binder = fields.getEditor().getBinder();
        Binder.Binding<ContentField, String> doneBinding = binder.
                //withValidator(contentField -> contentField.getName().length() > 3,"ERRR").
                bind(new TextField(), "name");

        fields.getColumn("name").setEditorBinding(doneBinding);
        fields.getColumn("type").setEditorComponent(new ComboBox<>("Type", asList(FieldType.values())));

        return new MVerticalLayout(addNew, fields);
    }

    @Override
    public List<ContentField> getValue() {
        return value;
    }

    @Override
    protected void doSetValue(List<ContentField> value) {
        this.value = value;
        updateFields();
    }

    private void updateFields() {
        fields.getEditor().cancel();
        fields.setRows(value);
    }

    private void addField(Button.ClickEvent clickEvent) {
        ContentField field = new ContentField();
        field.setContentKind(owner);
        field.setType(FieldType.TEXT);
        field.setName("new field");
        value.add(field);
        owner.setFields(value);
        updateFields();
    }

    public void owner(ContentKind owner) {
        this.owner = owner;
    }
}