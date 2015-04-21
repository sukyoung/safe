/*******************************************************************************
    Copyright (c) 2015, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
package kr.ac.kaist.jsaf.clone_refactor.eclipse;

import java.io.File;

import kr.ac.kaist.jsaf.clone_detector.util.CloneConfiguration;
import kr.ac.kaist.jsaf.clone_refactor.model.CloneCollection;
import kr.ac.kaist.jsaf.clone_refactor.views.CloneView;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.handlers.HandlerUtil;

public class AnalysisHandler extends AbstractHandler {
	private final String SEP = File.separator;
	public static String srcDir;

	/**
	 * The constructor.
	 */
	public AnalysisHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil
				.getActiveWorkbenchWindowChecked(event);

		CloneCollection.instance.clearGroups();
		UserInterface
				.showView("kr.ac.kaist.jsaf.clone_refactor.views.cloneview");
		UserInterface.showView(IConsoleConstants.ID_CONSOLE_VIEW);
		File jscd_config = new File(TaskManager.getPluginBase() + SEP + "bin"
				+ SEP + "jscd_config");
		CloneConfiguration config = new CloneConfiguration(jscd_config);
		ConfigurationDialog dialog = new ConfigurationDialog(window.getShell());

		if (dialog.open() == Window.OK) {
			CloneCollection.instance.clearGroups();
			CloneView.refresh();

			srcDir = dialog.getSrcDir();
			config.writeToConfig("SRC_DIR", srcDir);
			config.writeToConfig("MIN_TOKENS", dialog.getMinTokens());
			config.writeToConfig("SIMILARITY", "1.0");
			TaskManager.runJob(config);
		}
		return null;
	}
}
