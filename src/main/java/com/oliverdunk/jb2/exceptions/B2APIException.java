package com.oliverdunk.jb2.exceptions;

/**
 * Represents any error which is returned by the B2 API, and is thrown as a RuntimeException in order to give
 * clients the ability to fail gracefully.
 */
public class B2APIException extends RuntimeException {

    private String errorMessage, identifier;
    private int statusCode;

    /**
     * Constructs new B2APIException with the specified errorMessage.
     *
     * @param errorMessage An error message describing what went wrong
     */
    public B2APIException(String errorMessage){
        setErrorMessage(errorMessage);
    }

    /**
     * Sets the status code for this exception.
     *
     * @param statusCode The B2 error status represented by this exception
     */
    public void setStatusCode(int statusCode){
        this.statusCode = statusCode;
    }

    /**
     * Sets the identifier for this exception.
     *
     * @param identifier The B2 error code represented by this exception
     */
    public void setIdentifier(String identifier){
        this.identifier = identifier;
    }

    /**
     * Sets the error message for this exception.
     *
     * @param errorMessage An error message describing what went wrong
     */
    public void setErrorMessage(String errorMessage){
        this.errorMessage = errorMessage;
    }

    /**
     * Returns the status code for this exception.
     *
     * @return The B2 error status represented by this exception
     */
    public int getStatusCode(){
        return statusCode;
    }

    /**
     * Returns the identifier for this exception.
     *
     * @return The B2 error code represented by this exception
     */
    public String getIdentifier(){
        return identifier;
    }

    /**
     * Returns the error message for this exception.
     *
     * @return An error message describing what went wrong
     */
    public String getErrorMessage(){
        return errorMessage;
    }

}