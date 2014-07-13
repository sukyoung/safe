define('safe/visualization', ['require', 'exports', 'module' , 'ace/lib/fixoldbrowsers', 'ace/config', 'ace/lib/dom', 'ace/lib/net', 'ace/lib/lang', 'ace/lib/useragent', 'ace/lib/event', 'ace/theme/textmate', 'ace/edit_session', 'ace/undomanager', 'ace/keyboard/vim', 'ace/keyboard/emacs', 'ace/keyboard/hash_handler', 'ace/virtual_renderer', 'ace/editor', 'ace/multi_select', /*'kitchen-sink/doclist', 'kitchen-sink/modelist', 'kitchen-sink/layout', */'token_tooltip'/*, 'kitchen-sink/util', 'ace/split', 'kitchen-sink/statusbar'*/], function(require, exports, module) {

	require("ace/lib/fixoldbrowsers");
	require("ace/config").init();
	var env = {};

	var dom = require("ace/lib/dom");

	var event = require("ace/lib/event");
	var theme = require("ace/theme/eclipse");
	var EditSession = require("ace/edit_session").EditSession;
	var UndoManager = require("ace/undomanager").UndoManager;

	//var Editor = require("ace/editor").Editor;
	var MultiSelect = require("ace/multi_select").MultiSelect;

	var layout = require("./layout");
	var TokenTooltip = require("./token_tooltip").TokenTooltip;
	var container = document.getElementById("editor");
	env.editor = ace.edit("editor");
	window.env = env;
	window.ace = env.editor;
	env.editor.setAnimatedScroll(true);

	env.editor.setReadOnly(true);
	env.editor.setValue(file_contents, (0,0));
	env.editor.gotoLine(env.editor.session.getLength());
	
	env.editor.setTheme("ace/theme/eclipse");
	env.editor.getSession().setMode("ace/mode/javascript");
	require("ace/multi_select").MultiSelect(env.editor);
	
	document.getElementById('editor').style.fontSize='15px';
	
	env.editor.on("click", function() {
		var ps = editor.selection.getCursor();
		var column = ps.column;
		var row = ps.row;
	});


	var consoleEl = dom.createElement("div");
	container.parentNode.appendChild(consoleEl);
	consoleEl.style.cssText = "position:fixed; bottom:1px; right:0;\
	border:1px solid #baf; zIndex:100";

	var cmdLine = new layout.singleLineEditor(consoleEl);
	cmdLine.editor = env.editor;
	env.editor.cmdLine = cmdLine;
	env.editor.commands.addCommands([{
	    name: "gotoline",
	    bindKey: {win: "Ctrl-L", mac: "Command-L"},
	    exec: function(editor, line) {
	        if (typeof line == "object") {
	            var arg = this.name + " " + editor.getCursorPosition().row;
	            editor.cmdLine.setValue(arg, 1);
	            editor.cmdLine.focus();
	            return;
	        }
	        line = parseInt(line, 10);
	        if (!isNaN(line))
	            editor.gotoLine(line);
	    },
	    readOnly: true
	}, {
	    name: "find",
	    bindKey: {win: "Ctrl-F", mac: "Command-F"},
	    exec: function(editor, needle) {
	        if (typeof needle == "object") {
	            var arg = this.name + " " + editor.getCopyText();
	            editor.cmdLine.setValue(arg, 1);
	            editor.cmdLine.focus();
	            return;
	        }
	        editor.find(needle);
	    },
	    readOnly: true
	}, {
	    name: "focusCommandLine",
	    bindKey: "shift-esc",
	    exec: function(editor, needle) { editor.cmdLine.focus(); },
	    readOnly: true
	}, {
	    name: "execute",
	    bindKey: "ctrl+enter",
	    exec: function(editor) { 
	        try {
	            var r = eval(editor.getCopyText()||editor.getValue());
	        } catch(e) {
	            r = e;
	        }
	        editor.cmdLine.setValue(r + "")
	    },
	    readOnly: true
	}]);

	cmdLine.commands.bindKeys({
	    "Shift-Return|Ctrl-Return|Alt-Return": function(cmdLine) { cmdLine.insert("\n"); },
	    "Esc|Shift-Esc": function(cmdLine){ cmdLine.editor.focus(); },
	    "Return": function(cmdLine){
	        var command = cmdLine.getValue().split(/\s+/);
	        var editor = cmdLine.editor;
	        editor.commands.exec(command[0], editor, command[1]);
	        editor.focus();
	    }
	});

	cmdLine.commands.removeCommands(["find", "gotoline", "findall", "replace", "replaceall"]);

	var commands = env.editor.commands;
	commands.addCommand({
	    name: "save",
	    bindKey: {win: "Ctrl-S", mac: "Command-S"},
	    exec: function() {alert("Fake Save File");}
	});

    var editor = env.editor;
    editor.tokenTooltip = new TokenTooltip(editor);

	var StatusBar = require("./statusbar").StatusBar;
	new StatusBar(env.editor, cmdLine.container);

});

define('safe/statusbar', ['require', 'exports', 'module' , 'ace/lib/dom', 'ace/lib/lang'], function(require, exports, module) {

	var dom = require("ace/lib/dom");
	var lang = require("ace/lib/lang");

	var StatusBar = function(editor, parentNode) {
		this.element = dom.createElement("div");
		this.element.style.cssText = "color: gray; position:absolute; right:0; border-left:1px solid";
		parentNode.appendChild(this.element);

		var statusUpdate = lang.deferredCall(function(){
			this.updateStatus(editor)
		}.bind(this));
		editor.on("changeStatus", function() {
			statusUpdate.schedule(50);
		});
		editor.on("changeSelection", function() {
			statusUpdate.schedule(50);
		});
	};

	(function(){
		this.updateStatus = function(editor) {
			var status = [];
			function add(str, separator) {
				str && status.push(str, separator || "|");
			}
			
			if (editor.$vimModeHandler)
				add(editor.$vimModeHandler.getStatusText());
			else if (editor.commands.recording)
				add("REC");
			
			var c = editor.selection.lead;
			add(c.row + ":" + c.column, " ");
			if (!editor.selection.isEmpty()) {
				var r = editor.getSelectionRange();
				add("(" + (r.end.row - r.start.row) + ":"  +(r.end.column - r.start.column) + ")");
			}
			status.pop();
			this.element.textContent = status.join("");
		};
	}).call(StatusBar.prototype);

	exports.StatusBar = StatusBar;

	});

define('safe/layout', ['require', 'exports', 'module' , 'ace/lib/dom', 'ace/lib/event', 'ace/edit_session', 'ace/undomanager', 'ace/virtual_renderer', 'ace/editor', 'ace/multi_select', 'ace/theme/textmate'], function(require, exports, module) {
	var dom = require("ace/lib/dom");
	var event = require("ace/lib/event");

	var EditSession = require("ace/edit_session").EditSession;
	var UndoManager = require("ace/undomanager").UndoManager;
	var Renderer = require("ace/virtual_renderer").VirtualRenderer;
	var Editor = require("ace/editor").Editor;
	var MultiSelect = require("ace/multi_select").MultiSelect;

	dom.importCssString("\
	splitter {\
	    border: 1px solid #C6C6D2;\
	    width: 0px;\
	    cursor: ew-resize;\
	    z-index:10}\
	splitter:hover {\
	    margin-left: -2px;\
	    width:3px;\
	    border-color: #B5B4E0;\
	}\
	", "splitEditor");

	exports.edit = function(el) {
	    if (typeof(el) == "string")
	        el = document.getElementById(el);

	    var editor = new Editor(new Renderer(el, require("ace/theme/textmate")));

	    editor.resize();
	    event.addListener(window, "resize", function() {
	        editor.resize();
	    });
	    return editor;
	};


	var SplitRoot = function(el, theme, position, getSize) {
	    el.style.position = position || "relative";
	    this.container = el;
	    this.getSize = getSize || this.getSize;
	    this.resize = this.$resize.bind(this);

	    event.addListener(el.ownerDocument.defaultView, "resize", this.resize);
	    this.editor = this.createEditor();
	};

	(function(){
	    this.createEditor = function() {
	        var el = document.createElement("div");
	        el.className = this.$editorCSS;
	        el.style.cssText = "position: absolute; top:0px; bottom:0px";
	        this.$container.appendChild(el);
	        var session = new EditSession("");
	        var editor = new Editor(new Renderer(el, this.$theme));

	        this.$editors.push(editor);
	        editor.setFontSize(this.$fontSize);
	        return editor;
	    };
	    this.$resize = function() {
	        var size = this.getSize(this.container);
	        this.rect = {
	            x: size.left,
	            y: size.top,
	            w: size.width,
	            h: size.height
	        };
	        this.item.resize(this.rect);
	    };
	    this.getSize = function(el) {
	        return el.getBoundingClientRect();
	    };
	    this.destroy = function() {
	        var win = this.container.ownerDocument.defaultView;
	        event.removeListener(win, "resize", this.resize);
	    };


	}).call(SplitRoot.prototype);



	var Split = function(){

	};
	(function(){
	    this.execute = function(options) {
	        this.$u.execute(options);
	    };

	}).call(Split.prototype);



	exports.singleLineEditor = function(el) {
	    var renderer = new Renderer(el);
	    el.style.overflow = "hidden";
	    renderer.scrollBar.element.style.top = "0";
	    renderer.scrollBar.element.style.display = "none";
	    renderer.scrollBar.orginalWidth = renderer.scrollBar.width;
	    renderer.scrollBar.width = 0;
	    renderer.content.style.height = "auto";

	    renderer.screenToTextCoordinates = function(x, y) {
	        var pos = this.pixelToScreenCoordinates(x, y);
	        return this.session.screenToDocumentPosition(
	            Math.min(this.session.getScreenLength() - 1, Math.max(pos.row, 0)),
	            Math.max(pos.column, 0)
	        );
	    };

	    renderer.maxLines = 4;
	    renderer.$computeLayerConfigWithScroll = renderer.$computeLayerConfig;
	    renderer.$computeLayerConfig = function() {
	        var config = this.layerConfig;
	        var height = this.session.getScreenLength() * this.lineHeight;
	        if (config.height != height) {
	            var vScroll = height > this.maxLines * this.lineHeight;

	            if (vScroll != this.$vScroll) {
	                if (vScroll) {
	                    this.scrollBar.element.style.display = "";
	                    this.scrollBar.width = this.scrollBar.orginalWidth;
	                    this.container.style.height = config.height + "px";
	                    height = config.height;
	                    this.scrollTop = height - this.maxLines * this.lineHeight;
	                } else {
	                    this.scrollBar.element.style.display = "none";
	                    this.scrollBar.width = 0;
	                }

	                this.onResize();
	                this.$vScroll = vScroll;
	            }

	            if (this.$vScroll)
	                return renderer.$computeLayerConfigWithScroll();

	            this.container.style.height = height + "px";
	            this.scroller.style.height = height + "px";
	            this.content.style.height = height + "px";
	            this._emit("resize");
	        }

	        var longestLine = this.$getLongestLine();
	        var firstRow = 0;
	        var lastRow = this.session.getLength();

	        this.scrollTop = 0;
	        config.width = longestLine;
	        config.padding = this.$padding;
	        config.firstRow = 0;
	        config.firstRowScreen = 0;
	        config.lastRow = lastRow;
	        config.lineHeight = this.lineHeight;
	        config.characterWidth = this.characterWidth;
	        config.minHeight = height;
	        config.maxHeight = height;
	        config.offset = 0;
	        config.height = height;

	        this.$gutterLayer.element.style.marginTop = 0 + "px";
	        this.content.style.marginTop = 0 + "px";
	        this.content.style.width = longestLine + 2 * this.$padding + "px";
	    };
	    renderer.isScrollableBy=function(){return false};

	    renderer.setStyle("ace_one-line");
	    var editor = new Editor(renderer);
	    new MultiSelect(editor);
	    editor.session.setUndoManager(new UndoManager());

	    editor.setHighlightActiveLine(false);
	    editor.setShowPrintMargin(false);
	    editor.renderer.setShowGutter(false);
	    editor.renderer.setHighlightGutterLine(false);

	    editor.$mouseHandler.$focusWaitTimout = 0;

	    return editor;
	};



	});


define('safe/token_tooltip', ['require', 'exports', 'module' , 'ace/lib/dom', 'ace/lib/event', 'ace/range'], function(require, exports, module) {


	var dom = require("ace/lib/dom");
	var event = require("ace/lib/event");
	var Range = require("ace/range").Range;

	var tooltipNode;

	var TokenTooltip = function(editor) {
	    if (editor.tokenTooltip)
	        return;
	    editor.tokenTooltip = this;    
	    this.editor = editor;
	    
	    editor.tooltip = tooltipNode || this.$init();

	    this.update = this.update.bind(this);
	    this.onMouseMove = this.onMouseMove.bind(this);
	    this.onMouseOut = this.onMouseOut.bind(this);
	    this.onMouseDown = this.onMouseDown.bind(this);
	    event.addListener(editor.renderer.scroller, "mousemove", this.onMouseMove);
	    event.addListener(editor.renderer.content, "mouseout", this.onMouseOut);
	    event.addListener(editor.renderer.content, "mousedown", this.onMouseDown);
	};

	(function(){
	    this.token = {};
	    this.range = new Range();
	    this.irs = [];
	    
	    this.update = function() {
	        this.$timer = null;
	        
	        var r = this.editor.renderer;
	        if (this.lastT - (r.timeStamp || 0) > 1000) {
	            r.rect = null;
	            r.timeStamp = this.lastT;
	            this.maxHeight = innerHeight;
	            this.maxWidth = innerWidth;
	        }

	        var canvasPos = r.rect || (r.rect = r.scroller.getBoundingClientRect());
	        var offset = (this.x + r.scrollLeft - canvasPos.left - r.$padding) / r.characterWidth;
	        var row = Math.floor((this.y + r.scrollTop - canvasPos.top) / r.lineHeight);
	        var col = Math.round(offset);

	        var screenPos = {row: row, column: col, side: offset - col > 0 ? 1 : -1};
	        var session = this.editor.session;
	        var docPos = session.screenToDocumentPosition(screenPos.row, screenPos.column);
	        var token = session.getTokenAt(docPos.row, docPos.column);

	        if (!token && !session.getLine(docPos.row)) {
	            token = {
	                type: "",
	                value: "",
	                state: session.bgTokenizer.getState(0),
	            };
	        }
	        
	        if (!token) {
	            session.removeMarker(this.marker);
	            tooltipNode.style.display = "none";
	            this.isOpen = false;
	            return;
	        }
	        if (!this.isOpen) {
	            tooltipNode.style.display = "";
	            this.isOpen = true;
	        }
	        
	        var key = this.range.start.row+1;
	        var tokenText = "";
	        if (!isNaN(key) && ir[key] != undefined) {
	        	var curRow = this.range.start.row+1
	        	var curBCol = this.range.start.column+1
	        	var curECol = this.range.end.column
	        	var infos = irs[curRow];
	        	for(var i=0 ;i<infos.instsLen ; i++) {
	        		if (curRow==infos.insts[i].span.beginRow && infos.insts[i].span.beginCol<=curBCol) {
	        			if (curRow<infos.insts[i].span.endRow || curECol <= infos.insts[i].span.endCol) {
	        				this.irs.push(infos.insts[i]);
	        				tokenText += "["+infos.insts[i].iid+"] "+infos.insts[i].str + "\n";
	        			}
	        		}
	        	}
	        }

	        if (this.tokenText != tokenText) {
	            tooltipNode.textContent = tokenText;
	            this.tooltipWidth = tooltipNode.offsetWidth;
	            this.tooltipHeight = tooltipNode.offsetHeight;
	            this.tokenText = tokenText;
	        }
	        
	        this.updateTooltipPosition(this.x, this.y);

	        this.token = token;
	        session.removeMarker(this.marker);
	        this.range = new Range(docPos.row, token.start, docPos.row, token.start + token.value.length);
	        this.marker = session.addMarker(this.range, "ace_bracket", "text");
	    };
	    
	    this.onMouseMove = function(e) {
	        this.x = e.clientX;
	        this.y = e.clientY;
	        if (this.isOpen) {
	            this.lastT = e.timeStamp;
	            this.updateTooltipPosition(this.x, this.y);
	        }
	        if (!this.$timer)
	            this.$timer = setTimeout(this.update, 10);
	    };

	    this.onMouseOut = function(e) {
	        var t = e && e.relatedTarget;
	        var ct = e &&  e.currentTarget;
	        while(t && (t = t.parentNode)) {
	            if (t == ct)
	                return;
	        }
	        tooltipNode.style.display = "none";
	        this.editor.session.removeMarker(this.marker);
	        this.$timer = clearTimeout(this.$timer);
	        this.isOpen = false;
	    };

	    this.onMouseDown = function(e) {
	    	var curRow = this.range.start.row+1;
        	var curBCol = this.range.start.column+1;
        	var curECol = this.range.end.column;
    		var insts = this.getInstInfo(this.range);

	    	if (/*this.token.type === "storage.type" && */this.token.value === "function") {
				var old = window.location.href;
				var str = insts[0].str;
				window.location.href = old.replace("source.html","f"+str.split(":= function (")[1].split(")")[0]+".html");
	    	} else {
	    		var old = window.location.href;
	    		var sb = "";
	    		sb += old.replace("source.html","f"+insts[0].fid+".html");
	    		sb += "?"+insts[0].nodeid;
	    		sb += "&"+insts[0].str;
	    		window.location.href = sb;
	    	}
	    }

	    this.getInstInfo = function(range) {
	    	var curRow = range.start.row+1;
        	var curBCol = range.start.column+1;
        	var curECol = range.end.column;
        	var info = irs[curRow];
        	var insts = []
        	
        	for(var i=0 ;i<info.instsLen ; i++) {
        		if (curRow==info.insts[i].span.beginRow && info.insts[i].span.beginCol<=curBCol) {
        			if (curRow<info.insts[i].span.endRow || curECol <= info.insts[i].span.endCol) {
        				insts.push(info.insts[i]);
        			}
        		}
        	}
        	return insts;
	    }
	    
	    this.updateTooltipPosition = function(x, y) {
	        var st = tooltipNode.style;
	        if (x + 10 + this.tooltipWidth > this.maxWidth)
	            x = innerWidth - this.tooltipWidth - 10;
	        if (y > innerHeight * 0.75 || y + 20 + this.tooltipHeight > this.maxHeight)
	            y = y - this.tooltipHeight - 30;
	        
	        st.left = x + 10 + "px";
	        st.top = y + 20 + "px";
	    };

	    this.$init = function() {
	        tooltipNode = document.documentElement.appendChild(dom.createElement("div"));
	        var st = tooltipNode.style;
	        st.position = "fixed";
	        st.display = "none";
	        st.color = "#4682b4";
	        st.background = "#add8e6";
	        st.borderRadius = "4px";
	        //st.border = "1px solid lightgray";
	        st.padding = "1px";
	        st.zIndex = 1000;
	        st.fontFamily = "Consolas";
	        st.whiteSpace = "pre-line";
	        return tooltipNode;
	    };

	    this.destroy = function() {
	        this.onMouseOut();
	        event.removeListener(this.editor.renderer.scroller, "mousemove", this.onMouseMove);
	        event.removeListener(this.editor.renderer.content, "mouseout", this.onMouseOut);
	        event.removeListener(this.editor.renderer.content, "mousedown", this.onMouseDown);
	        delete this.editor.tokenTooltip;    
	    };

	}).call(TokenTooltip.prototype);

	exports.TokenTooltip = TokenTooltip;

	});

require("safe/visualization");

/**********************************************************************************************/

function Span(span) {
	var tokens = span.split(/:|-/);
	this.beginRow = tokens[0];
	this.beginCol = tokens[1];
	this.endRow = tokens[2];
	this.endCol = tokens[3];
	this.toString = function() {
		return this.beginRow+":"+beginCol+"-"+endRow+":"+endCol;
	}
}

function Inst(inst) {
	this.span = new Span(inst["span"]);
	this.iid = inst["iid"];
	this.fid = inst["fid"];
	this.nodeid = inst["nodeid"];
	this.str = inst["str"];
}

function SourceInfo(sourceinfo, linenum) {
	this.instsLen = sourceinfo.length;
	this.lineNum = linenum;
	this.insts = [];
	for (var i=0 ; i<this.instsLen ; i++) {
		this.insts[i] = new Inst(sourceinfo[i])
	}
}

var irs = (function parsingIR() {
	var irs = [];
	for (var i=0 ; i<ir.length ; i++) {
		irs[i] = new SourceInfo(ir[i][i], i);
	}
	return irs;
})()