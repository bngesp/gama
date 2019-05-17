package msi.gama.headless.batch.test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;

import com.google.common.collect.Multimap;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.headless.batch.AbstractModelLibraryRunner;
import msi.gama.headless.core.HeadlessSimulationLoader;
import msi.gama.headless.runtime.SystemLogger;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.kernel.experiment.TestAgent;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.validation.GamlModelBuilder;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.HeadlessListener;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.compilation.kernel.GamaBundleLoader;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.statements.test.TestState;

public class ModelLibraryTester extends AbstractModelLibraryRunner {

	private static ModelLibraryTester instance;
	private final static String FAILED_PARAMETER = "-failed";
//	public static final Logger LOGGER = Logger.getLogger(ModelLibraryTester.class.getName());;

	private ModelLibraryTester() {
//		SystemLogger.activeDisplay();
	}

	@Override
	public int start(final List<String> args) throws IOException { 

		SystemLogger.activeDisplay();
		HeadlessSimulationLoader.preloadGAMA();

		final int[] count = { 0 };
		final int[] code = { 0 };
		final boolean onlyFailed = args.contains(FAILED_PARAMETER);
		final boolean oldPref = GamaPreferences.Runtime.FAILED_TESTS.getValue();
		GamaPreferences.Runtime.FAILED_TESTS.set(onlyFailed);
		final Multimap<Bundle, String> plugins = GamaBundleLoader.getPluginsWithTests();
		List<URL> allURLs = new ArrayList<>();
		for (final Bundle bundle : plugins.keySet()) {
			for (final String entry : plugins.get(bundle)) {
				final Enumeration<URL> urls = bundle.findEntries(entry, "*", true);
				if (urls != null)
					while (urls.hasMoreElements()) {
						final URL url = urls.nextElement();
						if (isTest(url)) {
							final URL resolvedFileURL = FileLocator.toFileURL(url);
							allURLs.add(resolvedFileURL);
						}
					}
			}
		}
		GamlModelBuilder.loadURLs(allURLs);

		allURLs.forEach(u -> test(count, code, u));
		GamaPreferences.Runtime.FAILED_TESTS.set(oldPref);

//		LOGGER.info(
//				"" + count[0] + " tests executed in built-in library and plugins. " + code[0] + " failed or aborted");
		System.out.println(
				"" + count[0] + " tests executed in built-in library and plugins. " + code[0] + " failed or aborted");
		System.out.println(code[0]);
		return code[0];
	}

	public void test(final int[] count, final int[] code, final URL p) { 
//		System.out.println(p);
		final List<GamlCompilationError> errors = new ArrayList<>();
		try {
			final IModel model = GamlModelBuilder.compile(p, errors);
			if (model == null || model.getDescription() == null)
				return;
			final List<String> testExpNames = ((ModelDescription) model.getDescription()).getExperimentNames().stream()
					.filter(e -> model.getExperiment(e).isTest()).collect(Collectors.toList());

			if (testExpNames.isEmpty())
				return;
			for (final String expName : testExpNames) {
				final IExperimentPlan exp = GAMA.addHeadlessExperiment(model, expName, new ParametersSet(), null);
				if (exp != null) {
					final TestAgent agent = (TestAgent) exp.getAgent();
					exp.setHeadless(true);
					exp.getController().getScheduler().paused = false;
					exp.getAgent().step(agent.getScope());
					code[0] += agent.getSummary().countTestsWith(TestState.FAILED);
					code[0] += agent.getSummary().countTestsWith(TestState.ABORTED);
					count[0] += agent.getSummary().size();
					if (agent.getSummary().countTestsWith(TestState.FAILED) > 0
							|| agent.getSummary().countTestsWith(TestState.ABORTED) > 0)
						System.out.println(agent.getSummary().toString()); 
				}
			}
		} catch (final Exception ex) {
			System.out.println(ex.getMessage());
		}

	}

	public static ModelLibraryTester getInstance() {
		if (instance == null)
			instance = new ModelLibraryTester();
		return instance;
	}
}
