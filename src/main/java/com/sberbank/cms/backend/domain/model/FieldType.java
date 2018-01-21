package com.sberbank.cms.backend.domain.model;

import com.vaadin.data.*;
import com.vaadin.server.Setter;
import com.vaadin.ui.*;
import org.vaadin.viritin.fields.MTextField;

import java.time.LocalDate;
import java.util.Map;

public enum FieldType {
    TEXT, RICH_TEXT, DATE, BOOL;

    public AbstractField ui(String caption, Binder<Campaign> binder) {
        switch (this) {
            case TEXT:
                return bind(new MTextField(caption).withFullWidth(), binder, new NOOPConverter());
            case RICH_TEXT:
                RichTextArea textArea = new RichTextArea(caption);
                textArea.setSizeFull();
                return bind(textArea, binder, new NOOPConverter());
            case DATE:
                return bind(new DateField(caption), binder, new DateConverter());
            case BOOL:
                return bind(new CheckBox(caption, false), binder, new BoolConverter());
            default:
                return bind(new MTextField(caption).withFullWidth(), binder, new NOOPConverter());
        }
    }

    private AbstractField bind(AbstractField field, Binder<Campaign> binder, Converter converter) {
        binder.forField(field).withConverter(converter).
                bind((ValueProvider<Campaign, Object>) campaign -> campaign.getData().get(field.getCaption()),
                        setter(field));
        return field;
    }

    private Setter<Campaign, Object> setter(Component component) {
        return (campaign, val) -> {
            Map<String, Object> data = campaign.getData();
            data.put(component.getCaption(), val.toString());
            campaign.setData(data);
        };
    }

    private static class BoolConverter implements Converter {
        @Override
        public Result convertToModel(Object value, ValueContext context) {
            return Result.ok(value == null ? null : value.toString());
        }

        @Override
        public Object convertToPresentation(Object value, ValueContext context) {
            return value == null ? false : Boolean.valueOf(value.toString());
        }
    }

    private static class DateConverter implements Converter {
        @Override
        public Result convertToModel(Object value, ValueContext context) {
            return Result.ok(value == null ? null : value.toString());
        }

        @Override
        public Object convertToPresentation(Object value, ValueContext context) {
            return value == null ? null : LocalDate.parse(value.toString());
        }
    }

    private static class NOOPConverter implements Converter {
        @Override
        public Result convertToModel(Object value, ValueContext context) {
            return Result.ok(value == null ? null : value.toString());
        }

        @Override
        public Object convertToPresentation(Object value, ValueContext context) {
            return value == null ? "" : value;
        }
    }
}