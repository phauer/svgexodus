package de.philipphauer.svgexodus.util;

import org.apache.batik.apps.rasterizer.DestinationType;

/**
 * @author Philipp Hauer
 */
public class DestinationTypeUtil {

    /**
     * convertiert String (mime-type) in Destinationtype
     */
    public static DestinationType getDestinationType(String destinationType) {
        switch (destinationType) {
            case DestinationType.JPEG_STR:
                return DestinationType.JPEG;
            case DestinationType.PDF_STR:
                return DestinationType.PDF;
            case DestinationType.PNG_STR:
                return DestinationType.PNG;
            case DestinationType.TIFF_STR:
                return DestinationType.TIFF;
            default:
                throw new IllegalArgumentException("Invalid string for pDestinationType: " + destinationType);
        }
    }

}
