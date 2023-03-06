package customer.business_partner_validation.exceptions;

public class InvalidBPVerificationUpdateException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public InvalidBPVerificationUpdateException() {
        super();
    }

    public InvalidBPVerificationUpdateException(String message) {
        super(message);
    }

    public InvalidBPVerificationUpdateException(Throwable cause) {
        super(cause);
    }

    public InvalidBPVerificationUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidBPVerificationUpdateException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
    
}
