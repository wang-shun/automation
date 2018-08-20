package com.gome.test.api.ide.model.editor;

import com.gome.test.api.ide.model.TestCase;

import java.beans.PropertyEditorSupport;
import java.io.IOException;

import com.gome.test.utils.JsonUtils;

public class TestCaseEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        try {
            setValue(JsonUtils.readValue(text, TestCase.class));
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
    }

    @Override
    public String getAsText() {
        try {
            return JsonUtils.toJson(getValue());
        } catch (IOException ex) {
            return null;
        }
    }
}
