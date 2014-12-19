package de.philipphauer.svgexodus.model;

public class FeatureSwitch {

	public static final String CORRECTION_FILE_POSTFIX = "-corrected";

	private boolean correctionFeatureActived;

	public FeatureSwitch(boolean CorrectionFeatureActived) {
		this.correctionFeatureActived = CorrectionFeatureActived;
	}

	public boolean isCorrectionFeatureActived() {
		return correctionFeatureActived;
	}

}
