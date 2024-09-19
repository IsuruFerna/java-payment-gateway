package isuru.Technena.exceptions;

import java.util.UUID;

public class NotFoundException extends RuntimeException{
    public NotFoundException(UUID id) { super("User with id " + id + " doesn't found!");}

    public NotFoundException(String message) { super(message);}
}
