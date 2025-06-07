package be.kuleuven;

public class InvalidWedstrijdException extends RuntimeException {
  InvalidWedstrijdException(String indentificationString) {
    super("Invalid Wedstrijd met identification: " + indentificationString);
  }
}
