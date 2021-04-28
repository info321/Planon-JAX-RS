package us.planonsoftware.tms.dartmouth.webservice.jaxrs.dto;

public class CommonResponseDTO<T> {
    private boolean aIsSuccess;
    private String errorMessage;
    private T data;

    /**
     * @return the status
     */
    public boolean isSuccess() {
        return aIsSuccess;
    }

    /**
     * @param status the status to set
     */
    public void setIsSuccess(boolean isSuccess) {
        this.aIsSuccess = isSuccess;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * @return the data
     */
    public T getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(T data) {
        this.data = data;
    }
}
