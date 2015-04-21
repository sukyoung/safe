/*******************************************************************************
    Copyright (c) 2015, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
package kr.ac.kaist.jsaf.clone_refactor.clone;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import kr.ac.kaist.jsaf.clone_detector.util.CloneConfiguration;
import kr.ac.kaist.jsaf.clone_refactor.eclipse.TaskManager;
import kr.ac.kaist.jsaf.clone_refactor.eclipse.UserInterface;
import kr.ac.kaist.jsaf.clone_refactor.eclipse.Utility;
import kr.ac.kaist.jsaf.clone_refactor.model.CloneCollection;
import kr.ac.kaist.jsaf.clone_refactor.model.CloneGroup;
import kr.ac.kaist.jsaf.clone_refactor.model.CloneInstance;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CloneReader {
	private final static String SEP = File.separator;

	public static void readCloneReport() {
		try {
			File jscd_config = new File(TaskManager.getPluginBase() + SEP
					+ "bin" + SEP + "jscd_config");
			CloneConfiguration config = new CloneConfiguration(jscd_config);

			String token = config.getMinimumTokens().firstElement().toString();
			String stride = config.getStride().firstElement().toString();
			String similarity = config.getSimilarity().firstElement()
					.toString();

			File xmlfile = new File(TaskManager.getPluginBase()
					+ "build/clusters/post_cluster_vdb_" + token + "_" + stride
					+ "_allg_" + similarity + "_50.xml");

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlfile);
			doc.getDocumentElement().normalize();
			NodeList cloneset = doc.getElementsByTagName("cloneset");

			for (int i = 0; i < cloneset.getLength(); i++) {
				Node nNode = cloneset.item(i);
				CloneGroup group = new CloneGroup();

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;
					NodeList clonepart = eElement
							.getElementsByTagName("clonepart");

					for (int j = 0; j < clonepart.getLength(); j++) {
						Element element = (Element) clonepart.item(j);
						String filename = element.getAttribute("file");
						int startLine = Integer.parseInt(element
								.getAttribute("lineno"));
						int endLine = startLine
								+ Integer.parseInt(element
										.getAttribute("lineoffset")) - 1;
						CloneInstance instance = new CloneInstance(filename,
								startLine, endLine);

						if (!CloneCollection.contains(instance)) {
							instance.setId(CloneCollection.instance.size() + 1);
							group.add(instance);
						}
					}
				}
				if (group.size() >= 2) {
					try {
						if (Utility.isRefactorable(group)) {
							group.setId(CloneCollection.instance.size() + 1);
							CloneCollection.instance.add(group);
						}
					} catch (Exception e) {
						UserInterface.printStackTraceToConsole(e);
					}
				}
			}
		} catch (Exception e) {
			UserInterface.printStackTraceToConsole(e);
		}
	}
	
	
}
