package com.sap.slh.tax.attributes.determination.model.api;

public class DetailBuilder {
    private Detail instance;
    
    private DetailBuilder() {
        this.instance = new Detail();
    }
    
    public static DetailBuilder builder() {
        return new DetailBuilder();
    }
    
    public DetailBuilder withField(String field) {
        instance.setField(field);
        return this;
    }
    
    public DetailBuilder withMessage(String message) {
        instance.setMessage(message);
        return this;
    }
    
    public DetailBuilder withErrorCode(ValidationErrorCode errorCode) {
        instance.setErrorCode(errorCode);
        return this;
    }
    
    public Detail build(){
        return this.instance;
    }
}
