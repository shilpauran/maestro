package com.sap.slh.tax.maestro.api.assertion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorDetail;

public class HasMandatoryAttribute extends TypeSafeDiagnosingMatcher<Iterable<PropertyErrorDetail>> {

	private List<String> mandatoryAttributes;

	private HasMandatoryAttribute(List<String> mandatoryAttributes) {
		this.mandatoryAttributes = mandatoryAttributes;
	}

	public static HasMandatoryAttribute hasMandatoryItems(String... mandatoryAttributes) {
		return new HasMandatoryAttribute(Arrays.asList(mandatoryAttributes));
	}

	@Override
	public void describeTo(Description description) {
		Collections.sort(mandatoryAttributes);
		mandatoryAttributes.forEach(attribute -> description.appendValue(attribute));

	}

	@Override
	protected boolean matchesSafely(Iterable<PropertyErrorDetail> item, Description mismatchDescription) {
		List<String> attributesFound = new ArrayList<String>();
		for (PropertyErrorDetail invalidPropery : item) {
			if (mandatoryAttributes.contains(invalidPropery.getProperty())
					&& !attributesFound.contains(invalidPropery.getProperty())) {
				attributesFound.add(invalidPropery.getProperty());
			}
		}

		Collections.sort(attributesFound);
		attributesFound.forEach(attribute -> mismatchDescription.appendValue(attribute));
		return attributesFound.size() == mandatoryAttributes.size();
	}

}
