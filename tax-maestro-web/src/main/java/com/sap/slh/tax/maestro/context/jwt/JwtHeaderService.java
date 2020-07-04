package com.sap.slh.tax.maestro.context.jwt;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.sap.slh.tax.maestro.tax.exceptions.NoJwtProvidedException;

@Service
public class JwtHeaderService {
	
	public String getJwt(HttpHeaders headers) {
		List<String> authorizations = headers.get(HttpHeaders.AUTHORIZATION);
		String jwt;

		if (!CollectionUtils.isEmpty(authorizations) && !StringUtils.isEmpty(authorizations.get(0))) {
			jwt = authorizations.get(0);
		} else {
			throw new NoJwtProvidedException();
		}

		return jwt;
	}

}
