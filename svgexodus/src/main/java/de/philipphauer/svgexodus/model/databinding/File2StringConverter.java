/*
 * Copyright 2013 Philipp Hauer (philipphauer.de)
 */
package de.philipphauer.svgexodus.model.databinding;

import java.io.File;

import com.jgoodies.binding.value.BindingConverter;

/**
 * @author Philipp Hauer
 */
public class File2StringConverter implements BindingConverter<File, String> {

	@Override
	public String targetValue(File pSourceValue) {
		if (pSourceValue == null) {
			return "";
		}
		return pSourceValue.toString();
	}

	@Override
	public File sourceValue(String pTargetValue) {
		return new File(pTargetValue);
	}

}
