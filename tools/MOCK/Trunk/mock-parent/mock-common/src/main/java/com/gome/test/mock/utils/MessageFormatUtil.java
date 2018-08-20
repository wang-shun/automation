package com.gome.test.mock.utils;

import java.util.Locale;

public class MessageFormatUtil extends java.text.MessageFormat {

    private static final long serialVersionUID = 8595266361766092597L;

    public MessageFormatUtil(String pattern) {
        super(pattern);
    }

    public MessageFormatUtil(String pattern, Locale locale) {
        super(pattern, locale);
    }

}
