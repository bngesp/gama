/*******************************************************************************************************
 *
 * msi.gama.common.interfaces.IGamaView.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8)
 *
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.interfaces;

import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gaml.architecture.user.UserPanelStatement;
import msi.gaml.statements.test.CompoundSummary;

/**
 * An abstract representation of the 'views', in a UI sense, that are used to display outputs or present information to
 * the user. A view can display one or several outputs (for instance, several monitors)
 *
 * @author drogoul
 */
public interface IGamaView {

	public void update(IDisplayOutput output);

	public void addOutput(IDisplayOutput output);

	public void removeOutput(IDisplayOutput putput);

	IDisplayOutput getOutput();

	public void close(IScope scope);

	public void changePartNameWithSimulation(SimulationAgent agent);

	public void reset();

	public String getPartName();

	public void setName(String name);

	public void updateToolbarState();

	public interface Test {
		public void addTestResult(final CompoundSummary<?, ?> summary);

		public void startNewTestSequence(boolean all);

		public void displayProgress(int number, int total);

		public void finishTestSequence();

	}

	public interface Display {

		public boolean containsPoint(int x, int y);

		IDisplaySurface getDisplaySurface();

		public void toggleFullScreen();

		public boolean isFullScreen();

		void toggleSideControls();

		void toggleOverlay();

		void showOverlay();

		void hideOverlay();

		void hideToolbar();

		void showToolbar();

		LayeredDisplayOutput getOutput();

		public int getIndex();
		// {
		// final LayeredDisplayOutput output = getOutput();
		// return output == null ? 0 : output.getIndex();
		// }

		public void setIndex(int i);
		// {}
	}

	public interface Error {

		public void displayErrors();

	}

	public interface Html {
		public void setUrl(String url);
	}

	public interface Parameters {
		public void addItem(IExperimentPlan exp);

		public void updateItemValues();
	}

	public interface Console {

		void append(String msg, ITopLevelAgent root, GamaColor color);

	}

	public interface User {
		public void initFor(final IScope scope, final UserPanelStatement panel);
	}

}
