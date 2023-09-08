package com.homeProject.JennsArtWebsite.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class NoFileException extends RuntimeException {
    public NoFileException(String message) {
        super(message);
    }
}
