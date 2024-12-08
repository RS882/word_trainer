package com.word_trainer.exception_handler.not_found.exceptions;

import com.word_trainer.exception_handler.not_found.NotFoundException;

public class FolderNotFoundException extends NotFoundException {
    public FolderNotFoundException(String path) {
        super(String.format("Folder with path:  %s not found.", path));
    }
}
