package com.wallaceespindola.modernization.acl;

/** Anti-Corruption Layer: a legacy technical failure, translated into a domain-level error. */
public class LegacyTechnicalException extends RuntimeException {

    public LegacyTechnicalException(String message) {
        super(message);
    }

    public LegacyTechnicalException(String message, Throwable cause) {
        super(message, cause);
    }
}
