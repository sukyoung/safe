/******************************************************************************
 * Copyright (c) 2002 - 2011 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *****************************************************************************/
package kr.ac.kaist.jsaf.nodes_util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.ibm.wala.cast.js.html.IHtmlCallback;
import com.ibm.wala.cast.js.html.IHtmlParser;
import com.ibm.wala.cast.js.html.ITag;
import com.ibm.wala.cast.js.html.IUrlResolver;
import com.ibm.wala.cast.js.html.SourceRegion;
import com.ibm.wala.cast.js.html.WebUtil;
import com.ibm.wala.cast.tree.CAstSourcePositionMap.Position;
import com.ibm.wala.util.collections.Pair;

public class DomSimpleSourceExtractor {	
    private static final Pattern LEGAL_JS_IDENTIFIER_REGEXP =
        Pattern.compile("[a-zA-Z$_][a-zA-Z\\d$_]*");
    protected interface IGeneratorCallback extends IHtmlCallback {
        void writeToFinalRegion(SourceRegion finalRegion);
    }

    public static String getScriptName(String file) throws MalformedURLException {
        int lastIdxOfSlash = file.lastIndexOf('/');
        file = (lastIdxOfSlash == (-1)) ? file : file.substring(lastIdxOfSlash + 1);
        return file;
    }

    protected static class HtmlCallback implements IGeneratorCallback{
        protected final URL entrypointUrl;
        protected final IUrlResolver urlResolver;
        
        protected final SourceRegion scriptRegion;
        protected final SourceRegion domRegion;
        protected final SourceRegion entrypointRegion;
        
        private ITag currentScriptTag;
        private int nodeCounter = 0;
        private int scriptNodeCounter = 0;

        public HtmlCallback(URL entrypointUrl, IUrlResolver urlResolver) {
            this.entrypointUrl = entrypointUrl;
            this.urlResolver  = urlResolver;
            this.scriptRegion = new SourceRegion();
            this.domRegion = new SourceRegion();
            this.entrypointRegion = new SourceRegion();
        }

        protected Position makePos(int lineNumber, ITag governingTag) {
            return makePos(entrypointUrl, lineNumber, governingTag);
        }
           
        protected Position makePos(final URL url, final int lineNumber, ITag governingTag) {
            return governingTag.getElementPosition();
        }

        public void handleEndTag(ITag tag) {
            if (tag.getName().equalsIgnoreCase("script")) {
                assert currentScriptTag != null;
                currentScriptTag = null;
            }
        }

        public void handleText(Position p, String text) {
            if (currentScriptTag != null) {
                URL url = entrypointUrl;
                try {
                  url = new URL(entrypointUrl, "#" + scriptNodeCounter);
                } catch (MalformedURLException e) {
                  e.printStackTrace();
                }
                
            	text = text.replaceAll("[^\\x00-\\x7F]", "");
            	text = text.replaceAll("(?s)<!--.*?-->", "");
            	text = text.trim();
                if (text.startsWith("<![CDATA[")) {
                    assert text.endsWith("]]>");
                    text = text.substring(9, text.indexOf("]]>"));
                }
            	if (! text.trim().isEmpty())
            		writeToFile(url, p, text);
            }
        }

        public void handleStartTag(ITag tag) {
            if (tag.getName().equalsIgnoreCase("script") && tag.getAttributeByName("type") != null && tag.getAttributeByName("type").fst.equals("text/javascript")) {
                assert currentScriptTag == null;
                currentScriptTag = tag;
                scriptNodeCounter++;
            }
            handleDOM(tag);
        }

        /**
         * Model the HTML DOM
         * @param tag - the HTML tag to module
         */
        protected void handleDOM(ITag tag) {
            // Get the name of the modeling function either from the id attribute or a
            // running counter
            Pair<String,Position> idAttribute = tag.getAttributeByName("id");
            String funcName;
            if (idAttribute != null && LEGAL_JS_IDENTIFIER_REGEXP.matcher(idAttribute.fst).matches()) {
                funcName = idAttribute.fst;
            } else {
                funcName = "node" + (nodeCounter++);
            }
            handleDOM(tag, funcName);
        }

        protected void handleDOM(ITag tag, String funcName) {
            Map<String, Pair<String,Position>> attributeSet = tag.getAllAttributes();
        	for (Entry<String, Pair<String, Position>> a : attributeSet.entrySet()) {
                handleAttribute(a, funcName, tag);
            }
        }

        private void handleAttribute(Entry<String, Pair<String,Position>> a, String funcName, ITag tag) {
            URL url = entrypointUrl;
            try {
                url = new URL(entrypointUrl, "#" + tag.getElementPosition().getFirstOffset());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Position pos = a.getValue().snd;
            String attName = a.getKey();
            String attValue = a.getValue().fst;
            if (attName.toLowerCase().startsWith("on") ||
                (attValue != null && attValue.toLowerCase().startsWith("javascript:"))) {
                String fName = tag.getName().toLowerCase() + "_" + attName + "_" + funcName;
                String signatureLine = "function " + fName + "(event) {";
                domRegion.println(signatureLine + "\n" + extructJS(attValue) + "\n}", pos, url);
                entrypointRegion.println("\t" + fName + "(null);", pos, url);
            }
        }

        private String extructJS(String attValue) {
            if (attValue == null){
                return "";
            }
            String content;
            if (attValue.toLowerCase().equals("javascript:")) {
                content = attValue.substring("javascript:".length());
            } else {
                content = attValue;
            }
            return content;
        }

        public void writeToFinalRegion(SourceRegion finalRegion) {
            finalRegion.write(scriptRegion);
        }
        
        
        private void writeToFile(URL entrypointUrl, Position p, String text) {
        	String outputFileName = null;
			try {
				outputFileName = URLDecoder.decode(entrypointUrl.getFile(), "UTF-8");
				String extension = outputFileName.substring(outputFileName.lastIndexOf("."), outputFileName.length());
	        	outputFileName = outputFileName.substring(0, outputFileName.lastIndexOf(".")) + extension + "." + p.getFirstLine() + "_" + p.getLastLine() + ".js";
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (outputFileName != null) {
				File outputFile = new File(outputFileName);
				FileWriter fileWriter;
				BufferedWriter bufferedWriter;
				try {
					if (! outputFile.exists()) {
						fileWriter = new FileWriter(outputFile);
						bufferedWriter = new BufferedWriter(fileWriter);
						for (int i = 0; i < p.getFirstLine()-1;i++)
							bufferedWriter.newLine();
					} else {
						fileWriter = new FileWriter(outputFile, true);
						bufferedWriter = new BufferedWriter(fileWriter);
					}
					bufferedWriter.write(text);
					bufferedWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
        }
    }

    public void extractScripts(URL entrypointUrl, IHtmlParser htmlParser, IUrlResolver urlResolver) throws IOException {
    	InputStream inputStreamReader = WebUtil.getStream(entrypointUrl);
	    IGeneratorCallback htmlCallback = createHtmlCallback(entrypointUrl, urlResolver);
	    htmlParser.parse(entrypointUrl, inputStreamReader, htmlCallback, entrypointUrl.getFile());
    }
    
    protected IGeneratorCallback createHtmlCallback(URL entrypointUrl, IUrlResolver urlResolver) {
	    return new HtmlCallback(entrypointUrl, urlResolver);
	}
}
