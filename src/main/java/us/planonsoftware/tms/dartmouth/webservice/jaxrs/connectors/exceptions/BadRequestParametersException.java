package us.planonsoftware.tms.dartmouth.webservice.jaxrs.connectors.exceptions;

/**
 * Exception to throw when the request parameters validation/parsing fails.
 * 
 * @author sapydi
 *
 */
public class BadRequestParametersException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static final int BAD_REQUEST_STATUS_CODE = 400;

    /**
     * Creates an instance of the BadRequestParametersException
     * 
     * @param aMessage the error message.
     */
    public BadRequestParametersException(final String aMessage) {
        this(aMessage, null);
    }

    /**
     * Creates an instance of the BadRequestParametersException
     * 
     * @param aMessage the error message.
     * @param aThrowable the throwable.
     */
    public BadRequestParametersException(final String aMessage, final Throwable aThrowable) {
        super(aMessage, aThrowable);
    }

    /**
     * Returns the status code.
     * 
     * @return int the statusCode
     */
    public int getStatusCode() {
        return BAD_REQUEST_STATUS_CODE;
    }
}
