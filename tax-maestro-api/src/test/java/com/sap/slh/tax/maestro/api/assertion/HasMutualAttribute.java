package com.sap.slh.tax.maestro.api.assertion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorDetail;

public class HasMutualAttribute extends TypeSafeDiagnosingMatcher<Iterable<PropertyErrorDetail>> {

	private List<String> attributeNames;

	private HasMutualAttribute(List<String> attributeNames) {
	    this.attributeNames = attributeNames;
	}

	public static HasMutualAttribute hasMutualAttributes(String... attributeNames) {
		return new HasMutualAttribute(Arrays.asList(attributeNames));
	}

	@Override
	public void describeTo(Description description) {
		Collections.sort(attributeNames);
		attributeNames.forEach(attribute -> description.appendValue(attribute));

	}

	@Override
	protected boolean matchesSafely(Iterable<PropertyErrorDetail> item, Description mismatchDescription) {
		List<String> attributesFound = new ArrayList<String>();
		for (PropertyErrorDetail invalidProperty : item) {
		    for(String attr: attributeNames) {
		        if(invalidProperty.getMessage().contains(attr)) {
		            attributesFound.add(attr);
		        }
		    }
		}

		Collections.sort(attributesFound);
		attributesFound.forEach(attribute -> mismatchDescription.appendValue(attribute));
		return attributesFound.size() == attributeNames.size();
	}

}
