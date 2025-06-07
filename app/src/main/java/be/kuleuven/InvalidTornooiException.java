package be.kuleuven;

public class InvalidTornooiException extends RuntimeException {
  InvalidTornooiException(String indentificationString) {
    super("Invalid Speler met identification: " + indentificationString);
  }
}
