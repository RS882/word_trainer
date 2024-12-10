package com.word_trainer.constants.language;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.word_trainer.exception_handler.bad_requeat.exceptions.InvalidLanguageException;
import com.word_trainer.exception_handler.server_exception.ServerIOException;

import java.io.IOException;

public class LanguageDeserializer extends JsonDeserializer<Language> {

    @Override
    public Language deserialize(JsonParser parser, DeserializationContext context) {
        String value = "";
        try {
            value = parser.getText();
            return Language.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new InvalidLanguageException(value);
        } catch (IOException e) {
            throw new ServerIOException(e.getMessage());
        }
    }
}
