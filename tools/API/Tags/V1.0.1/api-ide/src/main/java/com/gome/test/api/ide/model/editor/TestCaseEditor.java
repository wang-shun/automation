package com.gome.test.api.ide.model.editor;

import com.gome.test.api.ide.model.TestCase;

import java.beans.PropertyEditorSupport;
import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

public class TestCaseEditor extends PropertyEditorSupport {

    private final ObjectMapper objMapper = new ObjectMapper();

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        try {
            setValue(objMapper.readValue(text, TestCase.class));
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
    }

    @Override
    public String getAsText() {
        try {
            return objMapper.writeValueAsString(getValue());
        } catch (IOException ex) {
            return null;
        }
    }
}
