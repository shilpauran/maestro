package com.sap.slh.tax.maestro.tax.exceptions.determine;

import java.io.Serializable;

import com.sap.slh.tax.attributes.determination.model.api.ValidationErrorCode;

public class ErrorDetail implements Serializable {

    private static final long serialVersionUID = -4773859394672203941L;
    private ValidationErrorCode errorCode;
    private String message;

    public ErrorDetail(ValidationErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public ValidationErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ValidationErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ErrorDetail [errorCode=");
        sb.append(errorCode);
        sb.append(", message=");
        sb.append(message);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((errorCode == null) ? 0 : errorCode.hashCode());
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ErrorDetail))
            return false;
        ErrorDetail other = (ErrorDetail)obj;
        if (errorCode != other.errorCode)
            return false;
        if (message == null) {
            if (other.message != null)
                return false;
        } else if (!message.equals(other.message))
            return false;
        return true;
    }

}
