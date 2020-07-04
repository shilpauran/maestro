package com.sap.slh.tax.maestro.api.common.exception;

import java.util.Iterator;
import java.util.List;

public class PropertyErrorMutualExclusionDetail extends PropertyErrorDetail {

    private static final String MSG_FORMAT = "Only one of the following attributes needs to be provided for '%s': [%s]";
    
    private final List<String> attributes;

   /**
    * {@link PropertyErrorMutualExclusionDetail} constructor.
    * 
    * @param property property name
    * @param attributes attributes names
    */
   public PropertyErrorMutualExclusionDetail(String property, List<String> attributes) {
       super(property);
       this.attributes = attributes;
   }
   
   public String getAttributes() {
       return getAttributesNames();
   }
    
    @Override
    public String getMessage() {
        return this.getMessageWithReferences(String.format(MSG_FORMAT, property, getAttributesNames()));
    }
    
    @Override
    public Object[] getAttributesAsArgs() {
        return new Object[] { this.getProperty(), this.getAttributes() };
    }
    
    private String getAttributesNames() {
        StringBuilder attributesNames = new StringBuilder();
        
        Iterator<String> iterator = this.attributes.iterator();
        while(iterator.hasNext()) {
            attributesNames.append(iterator.next());
            if(iterator.hasNext()) {
                attributesNames.append("; ");
            }
        }
        
        return attributesNames.toString();
    }

}
