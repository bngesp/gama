/*********************************************************************************************
 *
 * 'InfoTool.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/

package ummisco.gama.ui.viewers.gis.geotools.tool;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;
import java.util.WeakHashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;

import com.vividsolutions.jts.geom.Geometry;

import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.viewers.gis.geotools.control.JTextReporter;
import ummisco.gama.ui.viewers.gis.geotools.event.MapMouseEvent;
import ummisco.gama.ui.viewers.gis.geotools.utils.Utils;

/**
 * A cursor tool to retrieve information about features that the user clicks on with the mouse.
 * 
 * <p>
 * It works with {@code InfoToolHelper} objects which do the work of querying feature data. The primary reason for this
 * design is to shield this class from the grid coverage classes so that users who are working purely with vector data
 * are not forced to have JAI in the classpath.
 *
 * @see InfoToolHelper
 *
 * @author Michael Bedward
 * @since 2.6
 *
 *
 *
 * @source $URL$
 */
public class InfoTool extends CursorTool {

	/** The tool name */
	public static final String TOOL_NAME = "Info";
	/** Tool tip text */
	public static final String TOOL_TIP = "Click to identify features in selected layers";

	/**
	 * Default distance fraction used with line and point features. When the user clicks on the map, this tool searches
	 * for features within a rectangle of width w centred on the mouse location, where w is the average map side length
	 * multiplied by the value of this constant.
	 */
	public static final double DEFAULT_DISTANCE_FRACTION = 0.01d;

	// private Cursor cursor;

	private JTextReporter reporter;

	private final WeakHashMap<Layer, InfoToolHelper<?>> helperTable;

	/**
	 * Constructs a new info tool. To activate the tool only on certain mouse events provide a single mask, e.g.
	 * {@link SWT#BUTTON1}, or a combination of multiple SWT-masks.
	 *
	 * @param triggerButtonMask
	 *            Mouse button which triggers the tool's activation or {@value #ANY_BUTTON} if the tool is to be
	 *            triggered by any button
	 */
	public InfoTool(final int triggerButtonMask) {

		super(triggerButtonMask);

		helperTable = new WeakHashMap<>();
	}

	/**
	 * Constructs a new info tool which is triggered by any mouse button.
	 */
	public InfoTool() {
		this(CursorTool.ANY_BUTTON);
	}

	/**
	 * Respond to a mouse click by querying each of the {@code MapLayers}. The details of features lying within the
	 * threshold distance of the mouse position are reported on screen using a {@code JTextReporter} dialog.
	 * <p>
	 * <b>Implementation note:</b> An instance of {@code InfoToolHelper} is created and cached for each of the
	 * {@code MapLayers}. The helpers are created using reflection to avoid direct references to grid coverage classes
	 * here that would required JAI (Java Advanced Imaging) to be on the classpath even when only vector layers are
	 * being used.
	 *
	 * @param ev
	 *            mouse event
	 *
	 * @see JTextReporter
	 * @see InfoToolHelper
	 */
	@Override
	public void onMouseClicked(final MapMouseEvent ev) {

		if (!isTriggerMouseButton(ev)) { return; }

		final DirectPosition2D pos = ev.getMapPosition();
		report(pos);

		final MapContent context = getMapPane().getMapContent();
		for (final Layer layer : context.layers()) {
			if (layer.isSelected()) {
				InfoToolHelper<?> helper = null;

				String layerName = layer.getTitle();
				if (layerName == null || layerName.length() == 0) {
					layerName = layer.getFeatureSource().getName().getLocalPart();
				}
				if (layerName == null || layerName.length() == 0) {
					layerName = layer.getFeatureSource().getSchema().getName().getLocalPart();
				}

				helper = helperTable.get(layer);
				if (helper == null) {
					if (Utils.isGridLayer(layer)) {
						try {
							final Class<?> clazz =
									Class.forName("ummisco.gama.ui.viewers.gis.geotools.tool.GridLayerHelper");
							final Constructor<?> ctor = clazz.getConstructor(MapContent.class, Layer.class);
							helper = (InfoToolHelper<?>) ctor.newInstance(context, layer);
							helperTable.put(layer, helper);

						} catch (final Exception ex) {
							throw new IllegalStateException("Failed to create InfoToolHelper for grid layer", ex);
						}

					} else {
						try {
							final Class<?> clazz =
									Class.forName("ummisco.gama.ui.viewers.gis.geotools.tool.VectorLayerHelper");
							final Constructor<?> ctor = clazz.getConstructor(MapContent.class, Layer.class);
							helper = (InfoToolHelper<?>) ctor.newInstance(context, layer);
							helperTable.put(layer, helper);

						} catch (final Exception ex) {
							throw new IllegalStateException("Failed to create InfoToolHelper for vector layer", ex);
						}
					}
				}

				Object info = null;

				if (helper instanceof VectorLayerHelper) {
					final ReferencedEnvelope mapEnv = getMapPane().getDisplayArea();
					final double searchWidth = DEFAULT_DISTANCE_FRACTION * (mapEnv.getWidth() + mapEnv.getHeight()) / 2;
					try {
						info = helper.getInfo(pos, Double.valueOf(searchWidth));
					} catch (final Exception ex) {
						throw new IllegalStateException(ex);
					}

					if (info != null) {
						FeatureIterator<? extends Feature> iter = null;
						final SimpleFeatureCollection selectedFeatures = (SimpleFeatureCollection) info;
						try {
							iter = selectedFeatures.features();
							while (iter.hasNext()) {
								report(layerName, iter.next());
							}

						} catch (final Exception ex) {
							throw new IllegalStateException(ex);

						} finally {
							if (iter != null) {
								iter.close();
							}
						}
					}

				} else {
					try {
						info = helper.getInfo(pos);
					} catch (final Exception ex) {
						throw new IllegalStateException(ex);
					}

					if (info != null) {
						@SuppressWarnings ("unchecked") final List<Number> bandValues = (List<Number>) info;
						if (!bandValues.isEmpty()) {
							report(layerName, bandValues);
						}
					}
				}
			}
		}
	}

	/**
	 * Write the mouse click position to a {@code JTextReporter}
	 *
	 * @param pos
	 *            mouse click position (world coords)
	 */
	private void report(final DirectPosition2D pos) {
		createReporter();

		reporter.append(String.format("Pos \nx=%.4f \ny=%.4f\n\n", pos.x, pos.y));
	}

	/**
	 * Write the feature attribute names and values to a {@code JTextReporter}
	 *
	 * @param layerName
	 *            name of the map layer that contains this feature
	 * @param feature
	 *            the feature to report on
	 */
	private void report(final String layerName, final Feature feature) {
		createReporter();

		final Collection<Property> props = feature.getProperties();
		String valueStr = null;

		final StringBuilder sb = new StringBuilder();
		sb.append(layerName);
		sb.append("\n");

		for (final Property prop : props) {
			String name = prop.getName().getLocalPart();
			final Object value = prop.getValue();

			if (value instanceof Geometry) {
				name = "  Geometry";
				valueStr = value.getClass().getSimpleName();
			} else {
				valueStr = value.toString();
			}
			sb.append(name);
			sb.append(":");
			sb.append(valueStr);
			sb.append("\n\n");
		}
		reporter.append(sb.toString());
	}

	/**
	 * Write an array of grid coverage band values to a {@code JTextReporter}
	 *
	 * @param layerName
	 *            name of the map layer that contains the grid coverage
	 * @param bandValues
	 *            array of values
	 */
	private void report(final String layerName, final List<Number> bandValues) {
		createReporter();

		final StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append(layerName);
		sb.append("\n");

		int k = 1;
		for (final Number value : bandValues) {
			sb.append(String.format("  Band %d: %s\n", k++, value.toString()));
		}
		sb.append("\n\n");
		reporter.append(sb.toString());
	}

	/**
	 * Create and show a {@code JTextReporter} if one is not already active for this tool
	 */
	private void createReporter() {
		if (reporter == null || reporter.getShell() == null || reporter.getShell().isDisposed()) {
			final Shell shell =
					new Shell(WorkbenchHelper.getDisplay(), SWT.TITLE | SWT.CLOSE | SWT.BORDER | SWT.MODELESS);
			shell.setLayout(new GridLayout(1, false));
			reporter = new JTextReporter(shell, "Map info");
			reporter.open();
		}
	}

	@Override
	public boolean canDraw() {
		return false;
	}

	@Override
	public boolean canMove() {
		return false;
	}

}
