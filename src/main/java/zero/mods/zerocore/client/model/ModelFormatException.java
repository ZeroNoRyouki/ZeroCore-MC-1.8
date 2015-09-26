package zero.mods.zerocore.client.model;

public class ModelFormatException extends RuntimeException {

    public ModelFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModelFormatException(String message) {
        super(message);
    }

    public static ModelFormatException newParseError(String line, int lineNumber) {

        return new ModelFormatException(String.format("Error parsing entry ('%s', line %d) : Incorrect format", line, lineNumber));
    }

    public static ModelFormatException newNumberFormatError(int lineNumber, NumberFormatException ex) {

        return new ModelFormatException(String.format("Number formatting error at line %d", lineNumber), ex);
    }

    public static ModelFormatException newInvalidFace(String line, int lineNumber, int pointsExpected, int pointsFound) {

        return new ModelFormatException(String.format("Error parsing entry ('%s'" + ", line %d) : invalid number of points for face (expected %d, found %d)", line, lineNumber, pointsExpected, pointsFound));
    }

}
