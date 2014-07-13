/*******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf;

import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.tuple.Option;
import kr.ac.kaist.jsaf.analysis.typing.Config;
import kr.ac.kaist.jsaf.bug_detector.BugInfo;
import kr.ac.kaist.jsaf.bug_detector.StrictModeChecker;
import kr.ac.kaist.jsaf.compiler.*;
import kr.ac.kaist.jsaf.compiler.module.ModuleRewriter;
import kr.ac.kaist.jsaf.exceptions.*;
import kr.ac.kaist.jsaf.nodes.IRRoot;
import kr.ac.kaist.jsaf.nodes.Program;
import kr.ac.kaist.jsaf.nodes_util.*;
import kr.ac.kaist.jsaf.scala_src.useful.WorkManager;
import kr.ac.kaist.jsaf.shell.*;
import kr.ac.kaist.jsaf.useful.Pair;
import kr.ac.kaist.jsaf.useful.Triple;
import kr.ac.kaist.jsaf.useful.Useful;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public final class Shell {
    ////////////////////////////////////////////////////////////////////////////////
    // Settings and Environment variables
    ////////////////////////////////////////////////////////////////////////////////
    public static boolean                       debug = false;

    public static ShellParameters               params = new ShellParameters();
    public static boolean                       opt_DisambiguateOnly = false;
    public static String                        printTimeTitle = null;
    private static long                         startTime;

    public static WorkManager                   workManager = new WorkManager();
    public static Predefined                    pred;

    ////////////////////////////////////////////////////////////////////////////////
    // Main Entry point
    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Main entry point for the jsaf shell.
     * In order to support accurate testing of error messages, this method immediately
     * forwards to its two parameter helper method.
     * *** Please do not directly add code to this method, as it will interfere with testing.
     * *** Tests will silently fail.
     * *** Instead, add code to its helper method.
     */
    public static void main(String[] tokens) throws Throwable {
        // Call the internal main function
        main(false, tokens);
    }

    /**
     * Helper method that allows main to be called from tests
     * (without having to worry about System.exit).
     */
    public static void main(boolean runFromTests, String[] tokens) throws Throwable {
        int return_code = -1;

        // If there is no parameter then just print a usage message.
        if(tokens.length == 0) printUsageMessage();
        else return_code = subMain(tokens);

        // If there is an error and this main function is not called by the test
        //   then call the System.exit function to return the error code.
        if(return_code != 0 && !runFromTests) System.exit(return_code);
    }

    public static int subMain(String[] tokens) throws Throwable {
        // Now match the assembled string.
        int return_code = 0;
        try {
            // Parse parameters
            String errorMessage = params.Set(tokens);
            if(errorMessage != null) throw new UserError(errorMessage);
            pred = new Predefined(params);

            // Set the start time.
            startTime = System.currentTimeMillis();

            switch(params.command) {
            default :
            case ShellParameters.CMD_USAGE :
                printUsageMessage();
                break;
            case ShellParameters.CMD_PARSE :
                return_code = ParseMain.parse();
                break;
            case ShellParameters.CMD_UNPARSE :
                return_code = UnparseMain.unparse();
                break;
            case ShellParameters.CMD_TSPARSE :
                return_code = TSMain.tsparse();
                break;
            case ShellParameters.CMD_WIDLPARSE :
                return_code = WIDLMain.widlparse();
                break;
            case ShellParameters.CMD_TSCHECK :
                return_code = TSMain.tscheck();
                break;
            case ShellParameters.CMD_CLONE_DETECTOR :
                return_code = CloneDetectorMain.cloneDetector();
                break;
            case ShellParameters.CMD_COVERAGE :
                return_code = CoverageMain.coverage();
                break;
            case ShellParameters.CMD_CONCOLIC :
                return_code = ConcolicMain.concolic();
                break;
            case ShellParameters.CMD_URL :
                return_code = URLMain.url();
                break;
            case ShellParameters.CMD_WITH :
                return_code = WithMain.withRewriter();
                break;
            case ShellParameters.CMD_MODULE :
                return_code = ModuleMain.module();
                break;
            case ShellParameters.CMD_JUNIT :
                return_code = JUnitMain.junit();
                break;
            case ShellParameters.CMD_DISAMBIGUATE :
                opt_DisambiguateOnly = true;
                return_code = CompileMain.compile();
                break;
            case ShellParameters.CMD_COMPILE :
                CompileMain.compile();
                break;
            case ShellParameters.CMD_CFG :
                return_code = CFGMain.cfgBuilder();
                break;
            case ShellParameters.CMD_INTERPRET :
                return_code = InterpreterMain.interpret();
                break;
            case ShellParameters.CMD_ANALYZE :
            case ShellParameters.CMD_PREANALYZE :
            case ShellParameters.CMD_SPARSE :
            case ShellParameters.CMD_NEW_SPARSE :
                return_code = AnalyzeMain.analyze();
                break;
            case ShellParameters.CMD_HTML :
            case ShellParameters.CMD_HTML_PRE :
            case ShellParameters.CMD_HTML_SPARSE :
                return_code = AnalyzeMain.analyze();
                break;
            case ShellParameters.CMD_BUG_DETECTOR :
            case ShellParameters.CMD_WEBAPP_BUG_DETECTOR :
                return_code = AnalyzeMain.analyze();
                break;
            case ShellParameters.CMD_HELP :
                printHelpMessage();
                break;
            }
        } catch (ParserError e) {
            System.err.println(e);
            return_code = -1;
        } catch (StaticError e) {
            System.err.println(e);
            return_code = -1;
        } catch (UserError e) {
            System.err.println(e);
            return_code = -1;
        } /*catch (IOException error) {
            System.err.println(error.getMessage());
            return_code = -2;
        }*/

        // Print elapsed time.
        if(printTimeTitle != null)
            System.out.println(printTimeTitle + " took " + (System.currentTimeMillis() - startTime) + "ms.");

        return return_code;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Usage and Help messages
    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Helper method to print usage message.
     */
    private static void printUsageMessage() {
        System.err.print(
            "Usage:\n" +
            " parse [-out file] [-time] somefile.js ...\n" +
            " unparse [-out file] somefile.tjs\n" +
            " tsparse [-out somefile.db] somefile.ts\n" +
            " widlparse [-out somefile.db] {somefile.widl | somedir}\n" +
            " clone-detector\n" +
            " coverage somefile.js\n" +
            " concolic somefile.js\n" +
            " url [-out file] someurl\n" +
            " with [-out file] somefile.js ...\n" +
            " module [-out file] somefile.js ...\n" +
            " junit sometest.test ...\n" +
            " disambiguate [-out file] somefile.js ...\n" +
            " compile [-out file] [-time] somefile.js ...\n" +
            " cfg [-out file] [-test] [-model] [-dom] somefile.js(somefile.html) ...\n" +
            " interpret [-out file] [-time] [-mozilla] somefile.js ...\n" +
            " analyze [-verbose] [-test] [-memdump] [-exitdump] [-statdump] [-visual] [-checkResult]\n" +
            "         [-context-insensitive] [-context-1-callsite] [-context-1-object]\n" +
            "         [-context-tajs] [-unsound] [-nostop]\n" +
            "         somefile.js\n" +
            " html [-verbose] [-test] [-memdump] [-exitdump] [-statdump] [-visual] [-checkResult]\n" +
            "      [-context-insensitive] [-context-1-callsite] [-context-1-object]\n" +
            "      [-context-tajs] [-unsound] [-jq] [-domprop] [-scriptdump] [-out file] [-disableEvent] [-nostop] [-skipexternal]\n" +
            "      somefile.htm(l)\n" +
            " webapp-bug-detector [-dev] [-exitdump] [-disableEvent] [-timeout] [-nostop] somefile.htm(l)\n" +
            " bug-detector [-nostop] somefile.js\n" +
            "\n" +
            " help\n"
        );
    }

    /**
     * Helper method to print help message.
     */
    private static void printHelpMessage() {
        System.err.print
        ("Invoked as script: jsf args\n"+
         "Invoked by java: java ... kr.ac.kaist.jsaf.Shell args\n"+
         "jsaf parse [-out file] [-time] somefile.js ...\n"+
         "  Parses files. If parsing succeeds the message \"Ok\" will be printed.\n"+
         "  The files are concatenated in the given order before being parsed.\n"+
         "  If -out file is given, the parsed AST will be written to the file.\n"+
         "  If -time is given, the time it takes will be printed.\n"+
         "\n"+
         "jsaf unparse [-out file] somefile.tjs\n"+
         "  Converts a parsed file back to JavaScript source code. The output will be dumped to stdout if -out is not given.\n"+
         "  If -out file is given, the unparsed source code will be written to the file.\n"+
         "\n"+
         "jsaf tsparse [-out somefile.db] somefile.ts\n" +
         "  Parses a TypeScript file.\n"+
         "  If -out file is given, the parsed TypeScript will be written to the file.\n"+
         "\n"+
         "jsaf widlparse [-out somefile.db] {somefile.widl | somedir}\n" +
         "  Parses a Web IDL file or files in a directory.\n"+
         "  If -out file is given, the parsed Web IDL will be written to the file.\n"+
         "\n"+
         "jsaf widlcheck {-js somefile.js ... | -dir somedir} -db api1.db ...\n"+
         "  Checks uses of APIS described in Web IDL.\n"+
         "\n"+
         "jsaf clone-detector\n"+
         "  Runs the JavaScript clone detector.\n"+
         "\n"+
         "jsaf coverage somefile.js\n"+
         "  Calculates a very simple statement coverage.\n"+
         "\n"+
         "jsaf concolic somefile.js\n"+
         "  Working on a very simple concolic testing...\n"+
         "\n"+
         "jsaf url [-out file] someurl\n"+
         "  Extracts JavaScript source code from a url and writes it to a file, if any.\n"+
         "  If -out file is given, the extracted source code will be written to the file.\n"+
         "\n"+
         "jsaf with [-out file] somefile.js ...\n"+
         "  Rewrites JavaScript source codes using the with statement to another one without using the with statement.\n"+
         "  If it succeeds the message \"Ok\" will be printed.\n"+
         "  The files are concatenated in the given order before being parsed.\n"+
         "  If -out file is given, the rewritten source code will be written to the file.\n"+
         "\n"+
         "jsaf module [-out file] somefile.js ...\n"+
         "  Rewrites JavaScript source codes using the module syntax to another one without using the module syntax.\n"+
         "  The files are concatenated in the given order before being parsed.\n"+
         "  If -out file is given, the rewritten source code will be written to the file.\n"+
         "\n"+
         "jsaf junit somefile1.test ...\n"+
         "  Runs the system test file(s) somefile1.test (etc) in a junit textui harness.\n"+
         "\n"+
         "jsaf disambiguate [-out file] somefile.js ...\n"+
         "  Disambiguates references in JavaScript source files.\n"+
         "  The files are concatenated in the given order before being parsed.\n"+
         "  If -out file is given, the disambiguated AST will be written to the file.\n"+
         "\n"+
         "jsaf compile [-out file] [-time] somefile.js ...\n"+
         "  Translates JavaScript source files to IR.\n"+
         "  If the compilation succeeds the message \"Ok\" will be printed.\n"+
         "  The files are concatenated in the given order before being parsed.\n"+
         "  If -out file is given, the resulting IR will be written to the file.\n"+
         "  If -time is given, the time it takes will be printed.\n"+
         "\n"+
         "jsaf cfg [-out file] [-test] [-model] [-library] somefile.js(somefile.html) ...\n"+
         "  Builds a control flow graph for JavaScript source files.\n"+
         "  The files are concatenated in the given order before being parsed.\n"+
         "  If -out file is given, the resulting CFG will be written to the file.\n"+
         "  If -test is specified, predefined values for testing purpose will be provided.\n"+
         "  If -model is specified, the resulting CFG will include built-in models.\n"+
         "  If -library is specified, ...\n"+
         "\n"+
         "jsaf interpret [-out file] [-time] [-mozilla] somefile.js ...\n"+
         "  Interprets JavaScript files.\n"+
         "  If the interpretation succeeds the result will be printed.\n"+
         "  The files are concatenated in the given order before being parsed.\n"+
         "  If -out file is given, the parsed IR will be written to the file.\n"+
         "  If -time is given, the time it takes will be printed.\n"+
         "  If -mozilla is given, the shell files are prepended.\n"+
         "\n"+
         "jsaf analyze [-verbose] [-test] [-memdump] [-exitdump] [-statdump] [-visual] [-checkResult]\n"+
         "             [-context-insensitive] [-context-1-callsite] [-context-1-object]\n"+
         "             [-context-tajs] [-unsound] [-nostop]\n"+
         "             somefile.js\n"+
         "  Analyzes a JavaScript source.\n"+
         "\n"+
         "jsaf html [-verbose] [-test] [-memdump] [-exitdump] [-statdump] [-visual] [-checkResult]\n"+
         "          [-context-insensitive] [-context-1-callsite] [-context-1-object]\n"+
         "          [-context-tajs] [-unsound] [-jq] [-domprop] [-scriptdump] [-out file] [-disableEvent] [-nostop] [-skipexternal]\n"+
         "          somefile.htm(l)\n"+
         "  Analyzes JavaScript code in an HTML source.\n"+
         "\n"+
         "  If -verbose is specified, analysis results will be printed in verbose format.\n"+
         "  If -test is specified, predefined values for testing purpose will be provided.\n"+
         "  If -memdump is specified, result memory will be dumped to screen.\n"+
         "  If -exitdump is specified, result memory at the end will be dumped to screen.\n"+
         "  If -statdump is specified, statistics will be printed in dump format.\n"+
         "  If -visual is specified, result will be printed in web-based visualization format.\n"+
         "  If -checkResult is specified, expected result will be checked as in unit tests.\n"+
         "  If -context-insensitive is specified, context-sensitivity will be turned-off.\n"+
         "  If -context-1-callsite is specified, context-sensitivity will distinguish last callsite.\n"+
         "  If -context-1-object is specified, context-sensitivity will distinguish this values at last callsite.\n"+
         "  If -context-tajs is specified, TAJS-style 1-object context-sensitivity will be used.\n"+
         "  If -unsound is specified, unsound semantics is used.\n"+
         "  If -jq is specified, analysis will be performed with jQuery APIs loaded at the initial heap.\n"+
         "  If -domprop is specified, analysis will support the 'innerHTML' property updates of HTML elements.\n"+
         "  If -scriptdump is specified, script elements will be dumped to screen.\n"+
         "  If -out file is given, the dumped script elements will be written to the file.\n"+
         "  If -disableEvent is specified, analysis will be performed without any event trigger.\n"+
         "  If -nostop is specified, analysis will not stop by an exception.\n"+
         "  If -skipexternal is specified, analysis will ignore external files\n"+
         "\n"+
         "jsaf webapp-bug-detector [-dev] [-exitdump] [-disableEvent] [-nostop] somefile.htm(l)\n"+
         "  Reports possible bugs in JavaScript source files of web documents.\n"+
         "\n"+
         "  If -dev is not specified, only definite bugs will be reported\n"+
         "  If -dev is specified, all possible bugs will be reported\n"+
         "  If -exitdump is specified, result memory at the end will be dumped to screen.\n"+
         "  If -disableEvent is specified, bug detection will be performed without any event trigger.\n"+
         "  If -nostop is specified, analysis will not stop by an exception.\n"+
         "\n"+
         "jsaf bug-detector [-nostop] somefile.js\n"+
         "  Reports possible bugs in JavaScript source files.\n"+
         "  If -nostop is specified, analysis will not stop by an exception.\n"
        );
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Compile to IR
    ////////////////////////////////////////////////////////////////////////////////
    // Triple<String, Integer, String> : filename, starting line number, JavaScript source
    public static Option<IRRoot> scriptToIR(List<Triple<String, Integer, String>> scripts, Option<String> out) throws UserError, IOException {
        return scriptToIR(scripts, out, Option.<Coverage>none());
    }

    public static Option<IRRoot> scriptToIR(List<Triple<String, Integer, String>> scripts, Option<String> out, Option<Coverage> coverage) throws UserError, IOException {
        Program program = Parser.scriptToAST(scripts);
        return ASTtoIR(scripts.get(0).first(), program, out, coverage).first();
    }

    public static Option<IRRoot> fileToIR(List<String> files, Option<String> out) throws UserError, IOException {
        return fileToIR(files, out, Option.<Coverage>none());
    }

    public static Option<IRRoot> fileToIR(List<String> files, Option<String> out, Option<Coverage> coverage) throws UserError, IOException {
        Program program;
        // html file support 
        if(files.size() == 1 && (files.get(0).toLowerCase().endsWith(".html") || files.get(0).toLowerCase().endsWith(".xhtml") || files.get(0).toLowerCase().endsWith(".htm"))) { 
            // DOM mode
            Config.setDomMode();
            JSFromHTML jshtml = new JSFromHTML(files.get(0));
            // Parse JavaScript code in the target html file
            program = jshtml.parseScripts();
        } else program = Parser.fileToAST(files);

        // Program program = Parser.fileToAST(files);
        return ASTtoIR(files.get(0), program, out, coverage).first();
    }

    public static Triple<Option<IRRoot>, List<BugInfo>, Program> ASTtoIR(String file, Program pgm, Option<String> out, Option<Coverage> coverage) throws UserError, IOException {
        try {
            Program program = pgm;

            // Module Rewriter
            if (params.opt_Module) {
                ModuleRewriter moduleRewriter = new ModuleRewriter(program);
                program = (Program)moduleRewriter.doit();
            }

            // Hoister
            Hoister hoister = new Hoister(program);
            program = (Program)hoister.doit();
            List<BugInfo> shadowingErrors = hoister.getErrors();
            /* Testing Hoister...
            if (out.isSome()){
                String outfile = out.unwrap();
                try{
                    ASTIO.writeJavaAst(program, outfile);
                    System.out.println("Dumped hoisted code to " + outfile);
                } catch (IOException e){
                    throw new IOException("IOException " + e +
                                          "while writing " + outfile);
                }
            }
            */

            // Disambiguator
            Disambiguator disambiguator = new Disambiguator(program, opt_DisambiguateOnly);
            program = (Program)disambiguator.doit();
            List<StaticError> errors = disambiguator.getErrors();

            // Strict Mode Check
            switch(Shell.params.command) {
                case ShellParameters.CMD_ANALYZE :
                case ShellParameters.CMD_PREANALYZE :
                case ShellParameters.CMD_SPARSE :
                case ShellParameters.CMD_NEW_SPARSE :
                case ShellParameters.CMD_BUG_DETECTOR :
                case ShellParameters.CMD_WEBAPP_BUG_DETECTOR :
                case ShellParameters.CMD_HTML :
                case ShellParameters.CMD_HTML_SPARSE :
                    StrictModeChecker.clear();
                    StrictModeChecker.checkSimple(program);
            }

            // Testing Disambiguator...
            if (opt_DisambiguateOnly) {
                if (out.isSome()) {
                    String outfile = out.unwrap();
                    try {
                        Pair<FileWriter, BufferedWriter> pair = Useful.filenameToBufferedWriter(outfile);
                        FileWriter fw = pair.first();
                        BufferedWriter writer = pair.second();
                        writer.write(JSAstToConcrete.doitInternal(program));
                        writer.close();
                        fw.close();
                    } catch (IOException e){
                        throw new IOException("IOException " + e +
                                              "while writing " + outfile);
                    }
                } else if (errors.isEmpty()) {
                    System.out.println(JSAstToConcrete.doit(program));
                }
                reportErrors(NodeUtil.getFileName(program),
                             flattenErrors(errors),
                             Option.<Pair<FileWriter,BufferedWriter>>none());
                if (opt_DisambiguateOnly && errors.isEmpty())
                  return new Triple<Option<IRRoot>, List<BugInfo>, Program>(Option.some(IRFactory.makeRoot()),
                                                                            Useful.<BugInfo>list(),
                                                                            program);
                return new Triple<Option<IRRoot>, List<BugInfo>, Program>(Option.<IRRoot>none(),
                                                                          Useful.<BugInfo>list(),
                                                                          program);
            } else {
                WithRewriter withRewriter = new WithRewriter(program, false);
                program = (Program)withRewriter.doit();
                errors.addAll(withRewriter.getErrors());
                Translator translator = new Translator(program, coverage);
                IRRoot ir = (IRRoot)translator.doit();
                errors.addAll(translator.getErrors());
                if (errors.isEmpty()) {
                    return new Triple<Option<IRRoot>, List<BugInfo>, Program>(Option.some(ir),
                                                                              shadowingErrors,
                                                                              program);
                } else {
                    reportErrors(NodeUtil.getFileName(program),
                                 flattenErrors(errors),
                                 Option.<Pair<FileWriter,BufferedWriter>>none());
                    return new Triple<Option<IRRoot>, List<BugInfo>, Program>((params.opt_IgnoreErrorOnAST ? Option.some(ir) : Option.<IRRoot>none()),
                                                                              Useful.<BugInfo>list(),
                                                                              program);
                }
            }
        } catch (FileNotFoundException f) {
            throw new UserError(file + " not found");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Error Handling
    ////////////////////////////////////////////////////////////////////////////////
    public static List<? extends StaticError> flattenErrors(Iterable<? extends StaticError> ex) {
        List<StaticError> result = new LinkedList<StaticError>();
        for (StaticError err: ex) {
            result.addAll(flattenErrors(err));
        }
        return result;
    }

    public static List<? extends StaticError> flattenErrors(StaticError ex) {
        List<StaticError> result = new LinkedList<StaticError>();
        if (ex instanceof MultipleStaticError) {
            for (StaticError err : ((MultipleStaticError)ex).toJList())
                result.addAll(flattenErrors(err));
        } else result.add(new WrappedException(ex));
        return result;
    }

    public static int reportErrors(String file_name, List<? extends StaticError> errors,
                                   Option<Pair<FileWriter,BufferedWriter>> pair) throws IOException {
        int return_code = 0;
        if (!IterUtil.isEmpty(errors)) {
            for (StaticError error: IterUtil.sort(errors)) {
                if (pair.isSome()) pair.unwrap().second().write(error.getMessage());
                else System.out.println(error.getMessage());
            }
            String err_string;
            int num_errors = IterUtil.sizeOf(errors);
            if (num_errors == 0) {
                // Unreachable code?
                err_string = "File " + file_name + " compiled successfully.";
            } else {
                err_string = "File " + file_name + " has " + num_errors + " error" +
                    (num_errors == 1 ? "." : "s.");
            }
            if (pair.isSome()) pair.unwrap().second().write(err_string);
            else System.out.println(err_string);
            return_code = -2;
        }
        if (pair.isSome()) {
            pair.unwrap().second().close();
            pair.unwrap().first().close();
        }
        return return_code;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // etc
    ////////////////////////////////////////////////////////////////////////////////
    public static Option<String> toOption(String str) {
        if(str == null) return Option.<String>none();
        else return Option.<String>some(str);
    }
}
