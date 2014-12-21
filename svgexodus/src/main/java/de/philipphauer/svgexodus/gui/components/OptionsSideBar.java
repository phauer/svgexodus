package de.philipphauer.svgexodus.gui.components;

import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.apache.batik.apps.rasterizer.DestinationType;

import com.jgoodies.binding.beans.PropertyAdapter;
import com.jgoodies.binding.binder.BeanBinder;
import com.jgoodies.binding.binder.Binders;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueModel;

import de.philipphauer.svgexodus.gui.FloatToStringConverter;
import de.philipphauer.svgexodus.gui.GuiConst;
import de.philipphauer.svgexodus.model.FeatureSwitch;
import de.philipphauer.svgexodus.model.Options;
import de.philipphauer.svgexodus.model.databinding.File2StringConverter;

@SuppressWarnings("serial")
public class OptionsSideBar extends JPanel {

	private JTextField selectInputPathTextField;

	private JTextField widthTextField;

	private JTextField jpgQualityTextField;

	private JTextField outputPathTextField;

	private JCheckBox autoOpenResultCheckBox;

	private JCheckBox prefixWithParentFoldernameCheckBox;

	private JComboBox<DestinationType> destTypeCheckBox;

	private JCheckBox separateOutputPathCheckBox;

	private JButton selectInputButton;

	private JButton selectOutputButton;

	private JCheckBox correctionCheckBox;

	public OptionsSideBar() {
		buildGUI();
	}

	private void buildGUI() {
		setLayout(new MigLayout("fill, wrap 4"));

		JLabel lblPath = new JLabel("Input path:");
		selectInputPathTextField = new JTextField(45);
		selectInputButton = new JButton(GuiConst.ICON_FILE_SEARCH);
		JLabel lblDestType = new JLabel("Destination type:");
		destTypeCheckBox = new JComboBox<>();

		JLabel lblWidth = new JLabel("Width (px):");
		lblWidth.setToolTipText(GuiConst.TOOLTIP_WIDTH);
		widthTextField = new JTextField(20);
		widthTextField.setToolTipText(GuiConst.TOOLTIP_WIDTH);
		JLabel lblJpgQuality = new JLabel("JPG Quality:");
		lblJpgQuality.setToolTipText(GuiConst.TOOLTIP_HEIGHT);
		jpgQualityTextField = new JTextField(20);
		jpgQualityTextField.setToolTipText(GuiConst.TOOLTIP_HEIGHT);

		JLabel lblResultPathChb = new JLabel("Separate output folder:");
		separateOutputPathCheckBox = new JCheckBox();
		outputPathTextField = new JTextField(45);
		selectOutputButton = new JButton(GuiConst.ICON_FILE_SEARCH);
		JLabel lblAutoOpenResult = new JLabel("Automatically open result:");
		autoOpenResultCheckBox = new JCheckBox();
		JLabel lblPrefixWithParentFoldername = new JLabel("Prefix output file's name with containing folder:");
		lblPrefixWithParentFoldername.setToolTipText(GuiConst.TOOLTIP_PREFIX);
		prefixWithParentFoldernameCheckBox = new JCheckBox();

		add(lblPath);
		add(selectInputPathTextField, "span 2");
		add(selectInputButton);

		add(lblDestType);
		add(destTypeCheckBox, "span 3");
		add(lblWidth);
		add(widthTextField, "span 3");
		add(lblJpgQuality);
		add(jpgQualityTextField, "span 3");

		add(lblResultPathChb);
		add(separateOutputPathCheckBox);
		add(outputPathTextField);
		add(selectOutputButton);

		add(lblAutoOpenResult);
		add(autoOpenResultCheckBox, "span 3");
		add(lblPrefixWithParentFoldername);
		add(prefixWithParentFoldernameCheckBox, "span 3");
	}

	public void setOption(final Options options, FeatureSwitch featureSwitch) {
		final BeanBinder binder = Binders.binderFor(options);
		File2StringConverter file2StringConverter = new File2StringConverter();
		binder.bindProperty(Options.INPUT_PATH).converted(file2StringConverter).to(selectInputPathTextField);
		binder.bindProperty(Options.OUTPUT_FOLDER).converted(file2StringConverter).to(outputPathTextField);
		binder.bindProperty(Options.AUTOMATICALLY_OPEN_RESULTS).to(autoOpenResultCheckBox);
		binder.bindProperty(Options.PREFIX_WITH_PARENT_FOLDERNAME).to(prefixWithParentFoldernameCheckBox);
		binder.bindProperty(Options.WEIGTH).converted(FloatToStringConverter.get()).to(widthTextField);
		binder.bindProperty(Options.JPG_QUALITY).converted(FloatToStringConverter.get()).to(jpgQualityTextField);
		// Combobox for DestinationType
		List<DestinationType> destinationTypeList = Arrays.asList(new DestinationType[] { DestinationType.PDF,
				DestinationType.JPEG, DestinationType.PNG, DestinationType.TIFF });
		ValueModel countryModel = new PropertyAdapter<Options>(options, Options.DESTINATION_TYPE, true);
		SelectionInList<DestinationType> sil = new SelectionInList<DestinationType>(destinationTypeList, countryModel);
		binder.bind(sil).to(destTypeCheckBox);

		// enable/disable Textfield for ResultsFolder
		// kleiner fix: sonst wird initialer Zustand (wiederhergestellter zustand ist eigentlich
		// isSeparateResultFolder=false, aber
		// das initiale Update würde die Property überschreiben (auf true setzen).
		boolean separateResultFolder = options.isSeparateOutputFolder();
		separateOutputPathCheckBox.setSelected(separateResultFolder);
		outputPathTextField.setEnabled(separateResultFolder);
		selectOutputButton.setEnabled(separateResultFolder);
		// warum eigentlich so rum (compenten -> bean)? weil ich nur mit dem ersten para einen PropertyAdapter
		// übergeben kann, dem ich sagen kann,
		// dass es sich NICHT updaten soll, wenn sich sein Partner �ndert (false-Argument).
		separateOutputPathCheckBox.addChangeListener(event -> {
			boolean selected = separateOutputPathCheckBox.isSelected();
			outputPathTextField.setEnabled(selected);
			selectOutputButton.setEnabled(selected);
			if (!selected) {
				options.setOutputFolder(null);
			}
		});
		applyFeatureSwitch(options, featureSwitch);
	}

	public void actionStarted() {
		enableComponents(false);
	}

	public void actionStopped() {
		enableComponents(true);
	}

	private void enableComponents(boolean state) {
		selectInputButton.setEnabled(state);
		destTypeCheckBox.setEnabled(state);
		selectInputPathTextField.setEnabled(state);
		autoOpenResultCheckBox.setEnabled(state);
		widthTextField.setEnabled(state);
		jpgQualityTextField.setEnabled(state);
		prefixWithParentFoldernameCheckBox.setEnabled(state);
		if (correctionCheckBox != null) {
			correctionCheckBox.setEnabled(state);
		}
		// TODO only enable txfResultPath and btnLockupResult if options.isSeparateResultFolder()
		separateOutputPathCheckBox.setEnabled(state);
		outputPathTextField.setEnabled(state);
		selectOutputButton.setEnabled(state);
	}

	private void applyFeatureSwitch(Options options, FeatureSwitch featureSwitch) {
		if (featureSwitch.isCorrectionFeatureActived()) {
			final JLabel lblCorrection = new JLabel("Correct mistakes in svg:");
			correctionCheckBox = new JCheckBox();
			add(lblCorrection);
			add(correctionCheckBox);
			lblCorrection.setToolTipText(GuiConst.TOOLTIP_CORRECTION);
			correctionCheckBox.setToolTipText(GuiConst.TOOLTIP_CORRECTION);

			final BeanBinder binder = Binders.binderFor(options);
			binder.bindProperty(Options.SVG_MISTAKE_CORRECTION).to(correctionCheckBox);
		}
	}

	public void addLockupSourceListener(ActionListener actionListener) {
		selectInputButton.addActionListener(actionListener);
	}

	public String getSelectedSourcePath() {
		return selectInputPathTextField.getText();
	}

	public void addLockupResultListener(ActionListener actionListener) {
		selectOutputButton.addActionListener(actionListener);
	}

	public String getSelectedResultPath() {
		return outputPathTextField.getText();
	}

}
