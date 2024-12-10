package com.word_trainer.constants.language;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = LanguageDeserializer.class)
public enum Language {
    EN, DE, RU
}
