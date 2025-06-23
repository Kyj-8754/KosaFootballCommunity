package com.msa.do_login.user.util;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;


@Component
public class MapperUtil {
	private ModelMapper mapper;

	MapperUtil() {
		mapper = new ModelMapper();

		mapper.getConfiguration().setFieldAccessLevel(AccessLevel.PRIVATE)
								.setFieldMatchingEnabled(true)
								.setMatchingStrategy(MatchingStrategies.STRICT);
	}

	public <D> D map(Object source, Class<D> destinationType) {
		return mapper.map(source, destinationType);
	}
}
