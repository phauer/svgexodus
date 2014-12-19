package de.philipphauer.svgexodus.io.ser;

import java.io.IOException;

import de.philipphauer.svgexodus.model.Options;

public interface OptionsSerializer {

    void saveOptions(Options pOptions) throws IOException;

    Options loadOptions() throws Exception;

}
