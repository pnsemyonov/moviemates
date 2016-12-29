package lib.mates.pom.exceptions;

public class DeviceNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -7524663163366402920L;

    public DeviceNotFoundException() {
        super();
    }

    public DeviceNotFoundException(String message) {
        super(message);
    }
}
