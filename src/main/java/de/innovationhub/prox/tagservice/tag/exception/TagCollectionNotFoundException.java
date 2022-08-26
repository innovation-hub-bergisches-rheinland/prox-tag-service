package de.innovationhub.prox.tagservice.tag.exception;

import java.util.UUID;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = org.springframework.http.HttpStatus.NOT_FOUND)
public class TagCollectionNotFoundException extends RuntimeException {
    public TagCollectionNotFoundException(UUID id) {
        super("TagCollection not found: " + id);
    }
}
