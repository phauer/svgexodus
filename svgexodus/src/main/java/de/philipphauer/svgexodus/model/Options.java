package de.philipphauer.svgexodus.model;

import java.io.File;
import java.io.Serializable;

import org.apache.batik.apps.rasterizer.DestinationType;

import com.google.common.base.Objects;
import com.jgoodies.binding.beans.Model;

import de.philipphauer.svgexodus.util.DestinationTypeUtil;
import de.philipphauer.svgexodus.util.VersionUtil;

/**
 * @author Philipp Hauer
 */
public class Options extends Model implements Serializable {

	/** will cause a warning while converting. */
	private static final int DEFAULT_JPG_QUALITY = -1;

	/** uses width of input svg */
	private static final float DEFAULT_WIDTH = -1;

	/* derive serialVersionUID from App Version for better investigation of incompatible Options */
	private static final long serialVersionUID = VersionUtil.getAppVersionAsVersionUID();

	public static final String INPUT_PATH = "inputPath";
	public static final String OUTPUT_FOLDER = "outputFolder";
	public static final String AUTOMATICALLY_OPEN_RESULTS = "automaticallyOpenResults";
	public static final String DESTINATION_TYPE = "destinationType";
	public static final String SVG_MISTAKE_CORRECTION = "svgMistakeCorrection";
	public static final String PREFIX_WITH_PARENT_FOLDERNAME = "prefixWithParentFoldername";
	public static final String WEIGTH = "weight";
	public static final String JPG_QUALITY = "jpgQuality";

	/* Path is not serializable */
	private File inputPath;

	private File outputFolder;

	private boolean automaticallyOpenResults;

	private String destinationType;

	private boolean svgMistakeCorrection;

	/** <observed folder>/foo/bar.svg becomes <target folder>/foo_bar.pdf */
	private boolean prefixWithParentFoldername;

	private float weight = DEFAULT_WIDTH;

	private float jpgQuality = DEFAULT_JPG_QUALITY;

	private Options() {
	}

	public static Options create() {
		return new Options();
	}

	public Options withInputPath(File path) {
		inputPath = path;
		return this;
	}

	public Options withOutputFolder(File path) {
		outputFolder = path;
		return this;
	}

	public Options withAutomaticallyOpenResults(boolean autoOpenResults) {
		automaticallyOpenResults = autoOpenResults;
		return this;
	}

	public Options withDestinationType(DestinationType destType) {
		destinationType = destType.toString();
		return this;
	}

	public Options withSvgMistakeCorrection(boolean correction) {
		svgMistakeCorrection = correction;
		return this;
	}

	public Options withPrefixWithParentFoldername(boolean prefixWithParentFoldername) {
		this.prefixWithParentFoldername = prefixWithParentFoldername;
		return this;
	}

	public Options withWeight(float weight) {
		this.weight = weight;
		return this;
	}

	public Options withDefaultWeight() {
		this.weight = DEFAULT_WIDTH;
		return this;
	}

	public Options withJpgQuality(float jpgQuality) {
		this.jpgQuality = jpgQuality;
		return this;
	}

	public void setJpgQuality(float jpgQuality) {
		firePropertyChange(JPG_QUALITY, this.jpgQuality, this.jpgQuality = jpgQuality);
	}

	public void setWeight(float weight) {
		firePropertyChange(WEIGTH, this.weight, this.weight = weight);
	}

	public float getWeight() {
		return weight;
	}

	public float getJpgQuality() {
		return jpgQuality;
	}

	public File getInputPath() {
		return inputPath;
	}

	public void setInputPath(File inputPath) {
		firePropertyChange(INPUT_PATH, this.inputPath, this.inputPath = inputPath);
	}

	public File getOutputFolder() {
		return outputFolder;
	}

	public void setOutputFolder(File outputFolder) {
		firePropertyChange(OUTPUT_FOLDER, this.outputFolder, this.outputFolder = outputFolder);
	}

	public boolean isSeparateOutputFolder() {
		return outputFolder != null;
	}

	public boolean isAutomaticallyOpenResults() {
		return automaticallyOpenResults;
	}

	public void setAutomaticallyOpenResults(boolean automaticallyOpenResults) {
		firePropertyChange(AUTOMATICALLY_OPEN_RESULTS, this.automaticallyOpenResults,
				this.automaticallyOpenResults = automaticallyOpenResults);
	}

	public DestinationType getDestinationType() {
		return DestinationTypeUtil.getDestinationType(destinationType);
	}

	public void setDestinationType(DestinationType destinationType) {
		DestinationType oldDestinationType = DestinationTypeUtil.getDestinationType(this.destinationType);
		this.destinationType = destinationType.toString();
		firePropertyChange(DESTINATION_TYPE, oldDestinationType, destinationType);
	}

	public boolean isSvgMistakeCorrection() {
		return svgMistakeCorrection;
	}

	public void setSvgMistakeCorrection(boolean svgMistakeCorrection) {
		firePropertyChange(SVG_MISTAKE_CORRECTION, this.svgMistakeCorrection,
				this.svgMistakeCorrection = svgMistakeCorrection);
	}

	public boolean isPrefixWithParentFoldername() {
		return prefixWithParentFoldername;
	}

	public void setPrefixWithParentFoldername(boolean prefixWithParentFoldername) {
		this.prefixWithParentFoldername = prefixWithParentFoldername;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.addValue(inputPath)
				.addValue(outputFolder)
				.addValue(automaticallyOpenResults)
				.addValue(destinationType)
				.addValue(svgMistakeCorrection)
				.addValue(prefixWithParentFoldername)
				.addValue(weight)
				.addValue(jpgQuality)
				.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(automaticallyOpenResults, destinationType, jpgQuality, prefixWithParentFoldername,
				outputFolder, inputPath, svgMistakeCorrection, weight);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Options other = (Options) obj;
		return Objects.equal(this.automaticallyOpenResults, other.automaticallyOpenResults)
				&& Objects.equal(this.destinationType, other.destinationType)
				&& Objects.equal(this.jpgQuality, other.jpgQuality)
				&& Objects.equal(this.prefixWithParentFoldername, other.prefixWithParentFoldername)
				&& Objects.equal(this.outputFolder, other.outputFolder)
				&& Objects.equal(this.inputPath, other.inputPath)
				&& Objects.equal(this.svgMistakeCorrection, other.svgMistakeCorrection)
				&& Objects.equal(this.weight, other.weight);
	}

}
