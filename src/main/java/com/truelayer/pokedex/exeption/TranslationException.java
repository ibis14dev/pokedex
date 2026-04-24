package com.truelayer.pokedex.exeption;

public class TranslationException extends RuntimeException {
  public TranslationException(String message) {
    super(message);
  }

  public TranslationException(String message, Throwable cause) {
    super(message, cause);
  }
}
