package ummisco.gama.opengl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.jogamp.common.util.JarUtil;
import com.jogamp.opengl.GLProfile;

public class OpenGLActivator extends AbstractUIPlugin {

	public OpenGLActivator() {

		// // Necessary to load the native libraries correctly (see
		// //
		// http://forum.jogamp.org/Return-of-the-quot-java-lang-UnsatisfiedLinkError-Can-t-load-library-System-Library-Frameworks-glueg-td4034549.html)
		JarUtil.setResolver(url -> {
			try {
				final URL urlUnescaped = FileLocator.resolve(url);
				final URL urlEscaped = new URI(urlUnescaped.getProtocol(), urlUnescaped.getPath(), null).toURL();
				return urlEscaped;
			} catch (final IOException ioexception) {
				return url;
			} catch (final URISyntaxException urisyntaxexception) {
				return url;
			}
		});

		// Necessary to initialize very early because initializing it
		// while opening a Java2D view before leads to a deadlock
		final Job job = new Job("OpenGL Initialization") {

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				System.out.println(">GAMA Initializing OpenGL subsystem");
				GLProfile.initSingleton();	
				while (!GLProfile.isInitialized()) {
					try {
						System.out.println(">GAMA Initializing OpenGL subsystem");
						Thread.sleep(100);
					} catch (final InterruptedException e) {
						e.printStackTrace();
					}
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();

	}

}
