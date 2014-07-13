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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.ibm.wala.cast.js.html.IHtmlCallback;
import com.ibm.wala.cast.js.html.ITag;
import com.ibm.wala.cast.js.html.IUrlResolver;
import com.ibm.wala.cast.js.html.UrlManipulator;
import com.ibm.wala.cast.js.html.SourceRegion;
import com.ibm.wala.cast.js.html.UnicodeReader;
import com.ibm.wala.cast.tree.CAstSourcePositionMap.Position;
import com.ibm.wala.util.collections.Pair;

public class DomLessSourceExtractor {
    private static final Pattern LEGAL_JS_IDENTIFIER_REGEXP =
        Pattern.compile("[a-zA-Z$_][a-zA-Z\\d$_]*");
    interface IGeneratorCallback extends IHtmlCallback {
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
        private int counter = 0;

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
                if (text.startsWith("<![CDATA[")) {
                    assert text.endsWith("]]>");
                    text = text.substring(9, text.length()-11);
                }
                scriptRegion.println(text, currentScriptTag.getContentPosition(), entrypointUrl);
            }
          }
        
        public void handleStartTag(ITag tag) {
            if (tag.getName().equalsIgnoreCase("script")) {
                handleScript(tag);
                assert currentScriptTag == null;
                currentScriptTag = tag;
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
                funcName = "node" + (counter++);
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

        private String[] extructJS(String attValue) {
            if (attValue == null){
                return new String[] {};
            }
            String content;
            if (attValue.toLowerCase().equals("javascript:")) {
                content = attValue.substring("javascript:".length());
            } else {
                content = attValue;
            }
            return content.split("\\n");
        }

        protected void handleScript(ITag tag) {
            Pair<String,Position> value = tag.getAttributeByName("src");
            try {
                if (value != null) {
                    // script is out-of-line
                    getScriptFromUrl(value.fst, tag);
                }
            } catch (IOException e) {
                System.err.println("Error reading script file: " + e.getMessage());
            }
        }
        
        private void getScriptFromUrl(String urlAsString, ITag scriptTag)
            throws IOException, MalformedURLException {
            URL absoluteUrl = UrlManipulator.relativeToAbsoluteUrl(urlAsString, this.entrypointUrl);
            URL scriptSrc = urlResolver.resolve(absoluteUrl);
            if (scriptSrc == null) { //Error resolving URL
                return;
            }

            InputStream scriptInputStream = scriptSrc.openConnection().getInputStream();
            try {
                String line;
                BufferedReader scriptReader = new BufferedReader(new UnicodeReader(scriptInputStream, "UTF8"));
                while ((line = scriptReader.readLine()) != null) {
                    scriptRegion.println(line, scriptTag.getElementPosition(), scriptSrc);
                }
            } finally {
                scriptInputStream.close();
            }
        }

        public void writeToFinalRegion(SourceRegion finalRegion) {
            // wrapping the embedded scripts with a fake method of the window. Required for making this == window.
            finalRegion.println("window.__MAIN__ = function __WINDOW_MAIN__(){");
            finalRegion.write(scriptRegion);
            finalRegion.write(domRegion);
            finalRegion.println("  document.URL = new String(\"" + entrypointUrl + "\");");
            finalRegion.println("while (true){ ");
            finalRegion.write(entrypointRegion);
            finalRegion.println("} // while (true)");
            finalRegion.println("} // end of window.__MAIN__");
            finalRegion.println("window.__MAIN__();");
        }
    }
}
