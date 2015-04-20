package de.philipphauer.svgexodus.io.ser;

import de.philipphauer.svgexodus.model.Options;

public interface OptionsSerializer {

	void saveOptions(Options options) throws OptionsSerializerException;

	Options loadOptions() throws OptionsSerializerException;

}
