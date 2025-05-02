package be.kuleuven;

public class InvalidSpelerException extends RuntimeException {
  InvalidSpelerException(String indentificationString) {
    super("Invalid Speler met identification: " + indentificationString);
  }
}
