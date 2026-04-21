package com.syndicare.exceptions;

import jakarta.persistence.EntityNotFoundException;

public class ResourcesNotFoundException extends EntityNotFoundException {
    public ResourcesNotFoundException(String message) {
        super(message);
    }
}
