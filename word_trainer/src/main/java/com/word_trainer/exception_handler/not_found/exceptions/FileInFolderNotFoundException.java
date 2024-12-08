package com.word_trainer.exception_handler.not_found.exceptions;


import com.word_trainer.exception_handler.not_found.NotFoundException;

import java.util.UUID;

public class FileInFolderNotFoundException extends NotFoundException {
    public FileInFolderNotFoundException(UUID folderId) {
        super(String.format("Files not found in folder '%s'", folderId));
    }

}
