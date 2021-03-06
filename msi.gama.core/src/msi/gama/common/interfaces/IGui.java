/*******************************************************************************************************
 *
 * msi.gama.common.interfaces.IGui.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8)
 *
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.interfaces;

import java.util.List;
import java.util.Map;

import gnu.trove.map.hash.THashMap;
import msi.gama.common.interfaces.IDisplayCreator.DisplayDescription;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.IFileMetaDataProvider;
import msi.gaml.architecture.user.UserPanelStatement;
import msi.gaml.statements.test.CompoundSummary;
import msi.gaml.statements.test.TestExperimentSummary;
import msi.gaml.types.IType;

/**
 * The interface IGui. Represents objects that act on behalf of a concrete GUI implementation (RCP, Headless, etc.)
 *
 * @author drogoul
 * @since 18 dec. 2011
 *
 */
public interface IGui {

	public static final int ERROR = 0;
	public static final int WAIT = 1;
	public static final int INFORM = 2;
	public static final int NEUTRAL = 3;
	public static final int USER = 4;

	public static final Map<String, DisplayDescription> DISPLAYS = new THashMap<>();
	public static final String MONITOR_VIEW_ID = "msi.gama.application.view.MonitorView";
	public static final String INTERACTIVE_CONSOLE_VIEW_ID = "msi.gama.application.view.InteractiveConsoleView";
	public static final String AGENT_VIEW_ID = "msi.gama.application.view.AgentInspectView";
	public static final String TABLE_VIEW_ID = "msi.gama.application.view.TableAgentInspectView";
	public static final String LAYER_VIEW_ID = "msi.gama.application.view.LayeredDisplayView";
	public static final String GL_LAYER_VIEW_ID = "msi.gama.application.view.OpenGLDisplayView";
	public static final String GL_LAYER_VIEW_ID2 = "msi.gama.application.view.OpenGLDisplayView2";
	public static final String WEB_VIEW_ID = "msi.gama.application.view.WebDisplayView";
	public static final String ERROR_VIEW_ID = "msi.gama.application.view.ErrorView";
	public static final String TEST_VIEW_ID = "msi.gama.application.view.TestView";
	public static final String PARAMETER_VIEW_ID = "msi.gama.application.view.ParameterView";
	public static final String HEADLESSPARAM_ID = "msi.gama.application.view.HeadlessParam";
	public static final String HEADLESS_CHART_ID = "msi.gama.hpc.gui.HeadlessChart";
	public static final String NAVIGATOR_VIEW_ID = "msi.gama.gui.view.GamaNavigator";
	public static final String NAVIGATOR_LIGHTWEIGHT_DECORATOR_ID = "msi.gama.application.decorator";
	public static final String CONSOLE_VIEW_ID = "msi.gama.application.view.ConsoleView";
	public static final String USER_CONTROL_VIEW_ID = "msi.gama.views.userControlView";
	public static final String GRAPHSTREAM_VIEW_ID = "msi.gama.networks.ui.GraphstreamView";
	public static final String HPC_PERSPECTIVE_ID = "msi.gama.hpc.HPCPerspectiveFactory";

	public final static String PAUSED = "STOPPED";
	public final static String FINISHED = "FINISHED";
	public final static String RUNNING = "RUNNING";
	public final static String NOTREADY = "NOTREADY";
	public final static String ONUSERHOLD = "ONUSERHOLD";
	public final static String NONE = "NONE";
	public static final String PERSPECTIVE_MODELING_ID = "msi.gama.application.perspectives.ModelingPerspective";

	IStatusDisplayer getStatus(IScope scope);

	IConsoleDisplayer getConsole(IScope scope);

	IGamaView showView(IScope scope, String viewId, String name, int code);

	void tell(String message);

	void error(String error);

	void showParameterView(IScope scope, IExperimentPlan exp);

	void debug(String string);

	void clearErrors(IScope scope);

	void runtimeError(final IScope scope, GamaRuntimeException g);

	boolean confirmClose(IExperimentPlan experiment);

	boolean copyToClipboard(String text);

	boolean openSimulationPerspective(IModel model, String experimentId);

	public IDisplaySurface getDisplaySurfaceFor(final LayeredDisplayOutput output, final Object... args);

	Map<String, Object> openUserInputDialog(IScope scope, String title, Map<String, Object> initialValues,
			Map<String, IType<?>> types);

	void openUserControlPanel(IScope scope, UserPanelStatement panel);

	void closeDialogs(IScope scope);

	IAgent getHighlightedAgent();

	void setHighlightedAgent(IAgent a);

	void setSelectedAgent(IAgent a);

	void updateParameterView(IScope scope, IExperimentPlan exp);

	void prepareForExperiment(IScope scope, IExperimentPlan exp);

	void cleanAfterExperiment(IScope scope);

	void editModel(IScope scope, Object eObject);

	void runModel(final Object object, final String exp);

	void updateSpeedDisplay(IScope scope, Double d, boolean notify);

	IFileMetaDataProvider getMetaDataProvider();

	void closeSimulationViews(IScope scope, boolean andOpenModelingPerspective, boolean immediately);

	public DisplayDescription getDisplayDescriptionFor(final String name);

	String getExperimentState(String uid);

	void updateExperimentState(IScope scope, String state);

	void updateExperimentState(IScope scope);

	void updateViewTitle(IDisplayOutput output, SimulationAgent agent);

	void openWelcomePage(boolean b);

	void updateDecorator(String string);

	void run(String taskName, Runnable opener, boolean asynchronous);

	void setFocusOn(IShape o);

	void applyLayout(IScope scope, Object layout, Boolean keepTabs, Boolean keepToolbars, boolean showEditors);

	void displayErrors(IScope scope, List<GamaRuntimeException> newExceptions);

	ILocation getMouseLocationInModel();

	void setMouseLocationInModel(ILocation modelCoordinates);

	IGamlLabelProvider getGamlLabelProvider();

	void exit();

	void openInteractiveConsole(IScope scope);

	// Tests

	IGamaView.Test openTestView(IScope scope, boolean remainOpen);

	void displayTestsResults(IScope scope, CompoundSummary<?, ?> summary);

	public void endTestDisplay();

	public List<TestExperimentSummary> runHeadlessTests(final Object model);

	/**
	 * Tries to put the frontmost display in full screen mode or in normal view mode if it is already in full screen
	 *
	 * @return true if the toggle has succeeded
	 */
	boolean toggleFullScreenMode();

	void refreshNavigator();

	void hideScreen();

	void showScreen();

}
