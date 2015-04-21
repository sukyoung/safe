/*******************************************************************************
    Copyright (c) 2015, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
package kr.ac.kaist.jsaf.clone_refactor.eclipse;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import kr.ac.kaist.jsaf.clone_detector.util.CloneConfiguration;
import kr.ac.kaist.jsaf.clone_refactor.clone.CloneReader;
import kr.ac.kaist.jsaf.clone_refactor.core.AstTools;
import kr.ac.kaist.jsaf.clone_refactor.core.FunctionDeclarationProperty;
import kr.ac.kaist.jsaf.clone_refactor.core.Visitor;
import kr.ac.kaist.jsaf.clone_refactor.model.CloneCollection;
import kr.ac.kaist.jsaf.clone_refactor.model.CloneGroup;
import kr.ac.kaist.jsaf.clone_refactor.model.CloneInstance;
import kr.ac.kaist.jsaf.clone_refactor.views.CloneView;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.jsdt.core.dom.FunctionDeclaration;
import org.eclipse.wst.jsdt.core.dom.JavaScriptUnit;
import org.osgi.framework.Bundle;

public class TaskManager {
	public final static TaskManager instance = new TaskManager();
	private static MutexRule rule;
	private final static String SEP = File.separator;

	private TaskManager() {
		rule = new MutexRule();
	}

	public static void runJob(final CloneConfiguration config) {
		Job analysisJob = createAnalysisJob(config);
		Job summarizationJob = createSummarizationJob();
		analysisJob.setRule(rule);
		summarizationJob.setRule(rule);
		analysisJob.schedule();
		summarizationJob.schedule();
	}

	private static void runScript(final String script) {
		try {
			String[] command = script.split("\\s+");
			ProcessBuilder builder = new ProcessBuilder(command);
			builder.redirectErrorStream(true);
			Process process = builder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));

			String line = null;
			while ((line = reader.readLine()) != null) {
				UserInterface.out.println(line);
			}

			process.waitFor();
		} catch (Exception e) {
			UserInterface.printStackTraceToConsole(e);
		}
	}

	private class MutexRule implements ISchedulingRule {
		public boolean isConflicting(ISchedulingRule rule) {
			return rule == this;
		}

		public boolean contains(ISchedulingRule rule) {
			return rule == this;
		}
	}

	public static String getPluginBase() {
		Bundle bundle = Platform.getBundle("kr.ac.kaist.jsaf.clone_refactor");
		URL fileURL = bundle.getEntry("");
		try {
			URL resolvedFileURL = FileLocator.toFileURL(fileURL);
			URI resolvedURI = new URI(resolvedFileURL.getProtocol(),
					resolvedFileURL.getPath(), null);
			return new File(resolvedURI).getCanonicalPath() + SEP;
		} catch (Exception ioe) {
			UserInterface.printStackTraceToConsole(ioe);
			return "";
		}
	}

	private static Job createAnalysisJob(final CloneConfiguration config) {
		return new Job("Clone Analysis") {
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Clone analysis started...", 100);
				monitor.subTask("Clone Detection");
				String base = config.getBase();
				runScript("chmod +x " + base + "bin" + SEP + "js_home");
				runScript(base + "bin" + SEP + "js_home");
				runScript("chmod +x " + base + "bin" + SEP
						+ "fix_permissions.sh");
				runScript(base + "bin" + SEP + "fix_permissions.sh");
				runScript(base + "bin" + SEP
						+ "jsaf clone-detector -function -xml");
				monitor.worked(100);
				return Status.OK_STATUS;
			}
		};
	}

	private static Job createSummarizationJob() {
		return new Job("Summarizing Clones") {
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Job started...", 100);
				try {
					monitor.subTask("Reading clone report");
					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							CloneReader.readCloneReport();
						}
					});
					monitor.worked(40);

					monitor.subTask("Summarizing clones");
					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							CloneView.refresh();
						}
					});
					monitor.worked(60);
				} catch (Exception e) {
					UserInterface.printStackTraceToConsole(e);
				}
				monitor.done();
				return Status.OK_STATUS;
			}
		};
	}

	public static void performFinish() {
		Job job = new Job("Updating clone information") {
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Job started...", 100);

				monitor.subTask("Rebuilding clone data");
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						removeRefactoredGroup();
					}
				});
				monitor.worked(80);

				monitor.subTask("Refreshing clone view");
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						CloneView.refresh();
					}
				});
				monitor.worked(80);

				monitor.done();
				return Status.OK_STATUS;
			}
		};
		job.setRule(rule);
		job.schedule();
	}

	private static void removeRefactoredGroup() {
		ArrayList<CloneInstance> selectedInstances = CloneView
				.getSelectedInstances();

		int selectedIndex = FunctionDeclarationProperty.selectedIndex;
		CloneInstance baseInstance = selectedInstances.get(selectedIndex);
		int groupId = baseInstance.getId();

		JavaScriptUnit source = AstTools.parse(baseInstance, true);
		Visitor sourceVisitor = new Visitor(
				FunctionDeclarationProperty.functionName);
		source.accept(sourceVisitor);
		FunctionDeclaration function = (FunctionDeclaration) sourceVisitor
				.getTarget();
		int startLine = source.getLineNumber(function.getStartPosition());
		int endLine = source.getLineNumber(function.getStartPosition()
				+ function.getLength());

		ArrayList<CloneGroup> groups = CloneCollection.instance.getGroups();
		for (int i = 0; i < groups.size(); i++) {
			CloneGroup group = groups.get(i);

			if (group.getId() != groupId) {
				for (int j = 0; j < group.size(); j++) {
					ArrayList<CloneInstance> instances = group.getInstances();
					CloneInstance instance = instances.get(j);
					int offset = 0;

					for (int k = 0; k < selectedInstances.size(); k++) {
						CloneInstance selectedInstance = selectedInstances
								.get(k);

						if (instance.getFilename().equals(
								selectedInstance.getFilename())) {
							int selectedStartLine = selectedInstance
									.getStartLine();

							if (selectedInstance.getEndLine() < instance
									.getStartLine()) {
								JavaScriptUnit instanceAST = selectedInstance
										.getFullAST();
								int startPosition = selectedInstance
										.getAstNode().getStartPosition();
								selectedStartLine = instanceAST
										.getLineNumber(startPosition);
								offset -= selectedInstance.getEndLine()
										- selectedStartLine;
							}
						}
					}

					if (instance.getFilename().equals(
							baseInstance.getFilename())) {
						offset += endLine - startLine + 2;
					}

					instance.setStartLine(instance.getStartLine() + offset);
					instance.setEndLine(instance.getEndLine() + offset);
					instance.parse();
				}
			}
		}

		CloneGroup removalTarget = (CloneGroup) selectedInstances.get(0)
				.getParent();
		CloneCollection.instance.removeGroup(removalTarget);
		CloneView.clearSelectedInstances();
	}
}
