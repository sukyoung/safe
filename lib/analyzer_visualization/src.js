
// bundle of global variables.
var VISUAL = {};


/**
 * get Node object matched name.
 * @param name : node name
 * @returns
 */
function getNode(name) {
	for(var i=0 ; i<VISUAL.nodes.length ; i++) {
		if (VISUAL.nodes[i].name === name)
			return VISUAL.nodes[i];
	}
	return null;
}

/**
 * get all node names in CFG.
 * @returns {Array}
 */
function getAllNodesName() {
	var nodes = []
	for (var i=0 ; i<VISUAL.nodes.length ; i++) {
		nodes[i] = VISUAL.nodes[i].name;
	}
	return nodes;
}

/**
 * get all state names in node.
 * @param node
 * @returns {Array}
 */
function getStatesName(node) {	// State : (context, heap)
	var states = [];
	for (var i=0 ; i<node.stateLen ; i++) {
		states[i] = node.states[i].cc;
	}
	return states;
}

/**
 * apply folding to id
 * @param id
 */
function applyFolding(id) {
	$('li.folding'+id).addClass('.plusimageapply');
	$('li.folding'+id).children().addClass('noimage');
	$('li.folding'+id).children().hide();
	$('li.folding'+id).each(function(column){
		$(this).click(function(event) {
			if (this === event.target) {
				if ($(this).hasClass('.plusimageapply')) {
					$(this).children().show();
					$(this).removeClass('.plusimageapply');
					$(this).addClass('.minusimageapply');
				} else {
					$(this).children().hide();
					$(this).removeClass('.minusimageapply');
					$(this).addClass('.plusimageapply');
				}
			}
		});
	});
}


/**
 * create select tag with below params.
 * @param nodename node name
 * @param num dialog number
 * @param id use id of tag
 * @param values name of elements
 * @param defaultOpt name of defalut element
 * @param hSelect element selection handler
 * @returns {String} select tag
 */
function createSelect(nodename, num, id, values, defaultOpt, hSelect) {
	var sb = "";
	sb += "<select id='"+id+"' onChange=\"" + hSelect + "(this.value)\">";
	
	if (defaultOpt !== undefined)
		sb += "<option value=" + num + ":" + nodename + ":" + "default" + ">" + defaultOpt + "</option>";
	for(var i=0 ; i<values.length ; i++) {
		if (values[i] === nodename) {
			sb += "<option value=" + num + ":" + nodename + ":" + values[i] + " selected=\"true\">" + values[i] + "</option>";
		} else {
			sb += "<option value=" + num + ":" + nodename + ":" + values[i] + ">" + values[i] + "</option>";
		}
	}
	sb += "</select>";
	return sb;
}

function applyToolTip(nodename, statename, anchor) {
	var objs = getNode(nodename).getState(statename).heap.objs;
	
	anchor.href = "";
	for (var i=0 ; i<objs.length ; i++) {
		if (objs[i].name === anchor.text) {
			anchor.title = objs[i].toToolTip();
			return;
		}
	}
	anchor.title = "<li>undefined</li>";
}

/**
 * compare dialog-diff-L-content with dialog-diff-R-content. 
 * @param e
 */
function hDiffButtonClick(e) {
	var lnode = $('#dialog-diff-L-select1')[0].value.split(":")[2];
	var lstate = $('#dialog-diff-L-select2')[0].value.split(":")[2];
	var rnode = $('#dialog-diff-R-select1')[0].value.split(":")[2];
	var rstate = $('#dialog-diff-R-select2')[0].value.split(":")[2];
	
	if (lnode === "default" || lstate === "default" || rnode === "default" || rstate === "default" ||
			typeof lnode === "undefined" || typeof lstate === "undefined" || typeof rnode === "undefined" || typeof rstate === "undefined") {
		alert("need to select a node or a state.");
		return;
	}

	if (lnode === rnode && lstate === rstate) {
		alert("same state");
	}

	var diffs = stateDiff(getNode(lnode).getState(lstate), getNode(rnode).getState(rstate), "diff");
	
	$('#dialog-diff-L-content').children().remove();
	$('#dialog-diff-L-content').append(getNode(lnode).getState(lstate).toDiffTag("diff", diffs[0]));
	
	for (var i=0 ; i< $('#dialog-diff-L-content a').length ; i++)
		applyToolTip(lnode, lstate, $('#dialog-diff-L-content a')[i]);
	
	$('#dialog-diff-R-content').children().remove();
	$('#dialog-diff-R-content').append(getNode(rnode).getState(rstate).toDiffTag("diff", diffs[1]));

	for (i=0 ; i< $('#dialog-diff-R-content a').length ; i++)
		applyToolTip(rnode, rstate, $('#dialog-diff-R-content a')[i]);

	$(function() {
		$("#dialog-diff-L-content a").tooltip({
			track: true,
			delay: 0,
			showURL: false,
			top: 10,
			left: 10
		})
		$("#dialog-diff-R-content a").tooltip({
			track: true,
			delay: 0,
			showURL: false,
			top: 10,
			left: 10
		})
	});
}

/**
 * reset Diff dialog.
 * @param e
 */
function hResetDiff(e) {
	$('#dialog-diff-L-content').children().remove();
	$('#dialog-diff-R-content').children().remove();
	$('#dialog-diff-L-select1')[0].selectedIndex = 0;
	$('#dialog-diff-L-select2')[0].selectedIndex = 0;
	$('#dialog-diff-R-select1')[0].selectedIndex = 0;
	$('#dialog-diff-R-select2')[0].selectedIndex = 0;
}

/**
 * calculate states and redraw content when node was selected in select box.
 * @param e "LR:node:selection"
 */
function hDiffSelect1Changed(e) {
	// LR:node:state
	var value = e.split(":");
	var LR = value[0];
	var node = value[2];

	$('#dialog-diff-'+LR+'-select2').children().remove();	// clear state elements
	$('#dialog-diff-'+LR+'-content').children().remove();	// clear contents

	if (node !== "default") {
		var states = getStatesName(getNode(node));
		
		$('#dialog-diff-'+LR+'-select2').append("<option value=" + LR + ":" + node + ":" + "default" + " selected='true'>" + "State" + "</option>");
		for (var i=0 ; i<states.length ; i++) {
			$('#dialog-diff-'+LR+'-select2').append("<option value=" + LR + ":" + node + ":" + states[i] + ">" + states[i] + "</option>");
		}
	}
}

/**
 * redraw content when state was selected in select box.
 * @param e "LR:node:selection"
 */
function hDiffSelect2Changed(e) {
	// LR:node:state
	var value = e.split(":");
	var LR = value[0];
	var node = value[1];
	var state = value[2];

	$('#dialog-diff-'+LR+'-content').children().remove();	// clear contents

	if (state !== "default") {
		$('#dialog-diff-'+LR+'-content').append(getNode(node).getState(state).toTag('diff'+LR));
		applyFolding('diff'+LR);
	}
}

/**
 * remove old elements and fill new elements in select box.  
 * @param LR "L" or "R"
 * @param nodesname array of nodes name.
 * @param selectnode selected node name.
 * @param statesname array of states name.
 * @param selectstate selected state name.
 */
function fillDiffSelects(LR, nodesname, selectnode, statesname, selectstate) {
	$('#dialog-diff-'+LR+'-select1').children().remove();

	$('#dialog-diff-'+LR+'-select1').append("<option value=" + LR + ":" + selectnode + ":" + "default" + ">" + "Node" + "</option>");
	for (var i=0 ; i<nodesname.length ; i++) {
		if (nodesname[i] === selectnode)
			$('#dialog-diff-'+LR+'-select1').append("<option value=" + LR + ":" + selectnode + ":" + nodesname[i] + " selected=\"true\">" + nodesname[i] + "</option>");
		else $('#dialog-diff-'+LR+'-select1').append("<option value=" + LR + ":" + selectnode + ":" + nodesname[i] + ">" + nodesname[i] + "</option>");
	}

	$('#dialog-diff-'+LR+'-select2').children().remove();
	$('#dialog-diff-'+LR+'-select2').append("<option value=" + LR + ":" + selectnode + ":" + "default" + ">" + "State" + "</option>");
	for (i=0 ; i<statesname.length ; i++) {
		if (statesname[i] === selectstate)
			$('#dialog-diff-'+LR+'-select2').append("<option value=" + LR + ":" + selectnode + ":" + statesname[i] + " selected=\"true\">" + statesname[i] + "</option>");
		else $('#dialog-diff-'+LR+'-select2').append("<option value=" + LR + ":" + selectnode + ":" + statesname[i] + ">" + statesname[i] + "</option>");
	}
}

/**
 * remove old contents and fill new contents in dialog.  
 * @param LR LR "L" or "R"
 * @param node selectnode selected node name.
 * @param state selectstate selected state name.
 */
function fillDiffContent(LR, node, state) {
	$('#dialog-diff-'+LR+'-content').children().remove();

	$('#dialog-diff-'+LR+'-content').append(getNode(node).getState(state).toTag('diff-'+LR));

	for (i=0 ; i< $('#dialog-diff-'+LR+'-content a').length ; i++)
		applyToolTip(node, state, $('#dialog-diff-'+LR+'-content a')[i]);
	applyFolding('diff-'+LR);

	$(function() {
		$('#dialog-diff-'+LR+'-content a').tooltip({
			track: true,
			delay: 0,
			showURL: false,
			top: 10,
			left: 10
		})
	});
}

/**
 * create dialog tag for diff view.
 * @param parent parent element for this doalog.
 * @param dialog number occurred event.
 * @param dialogOpts css option for this dialog.
 */
function createDiffDialog(parent, dlgid, dialogOpts) {
	$(parent).append("<div id='dialog-diff'></div>");
	$('#dialog-diff').dialog(dialogOpts);

	// dialog-diff-#-L
	$('#dialog-diff').append("<div id='dialog-diff-L' class=\"ui-widget ui-widget-content ui-corner-all\" style=\"float:left; width:49%; min-width:300px;\"></div>");
	$('#dialog-diff-L').append("<select id='dialog-diff-L-select1' onChange='hDiffSelect1Changed(this.value)'></select>");	$('#dialog-diff-L-select1')[0].style.marginLeft = "5px";
	$('#dialog-diff-L').append("<select id='dialog-diff-L-select2' onChange='hDiffSelect2Changed(this.value)'></select>");	$('#dialog-diff-L-select2')[0].style.marginLeft = "10%";
	$('#dialog-diff-L').append("</br></br>");
	$('#dialog-diff-L').append("<div id='dialog-diff-L-content'><div>");

	// dialog-diff-#-R
	$('#dialog-diff').append("<div id='dialog-diff-R' class=\"ui-widget ui-widget-content ui-corner-all\" style=\"float:right; width:49%; min-width:300px;\"></div>");
	$('#dialog-diff-R').append("<select id='dialog-diff-R-select1' onChange='hDiffSelect1Changed(this.value)'></select>");	$('#dialog-diff-R-select1')[0].style.marginLeft = "5px";
	$('#dialog-diff-R').append("<select id='dialog-diff-R-select2' onChange='hDiffSelect2Changed(this.value)'></select>");	$('#dialog-diff-R-select2')[0].style.marginLeft = "10%";	
	$('#dialog-diff-R').append("</br></br>");
	$('#dialog-diff-R').append("<div id='dialog-diff-R-content'><div>");
	
}

/**
 * move to Diff dialog when "move to Diff" button was clicked in Node dialog.
 * @param e
 */
function hMoveToDiff(e) {
	var node = $('#'+this.id+'-select1')[0].value.split(":")[2];
	var state = $('#'+this.id+'-select2')[0].value.split(":")[2];

	if (node === "default" || state === "default") {
		alert("need to select a node or a state.");
		return;
	}

	var dialogOpts = {
			title: "Diff",
			autoOpen: true,
			closeOnEscape: true,
			closeText: "x",
			//position: [e.clientX, e.clientY],
			position: "center",
			show: "blind",
			hide: "explode",
			minWidth : "651",
			width : "651",
			height : "300",
			stack:true,
			resize:true,
			buttons:{"diff":hDiffButtonClick, "reset":hResetDiff}
		};
	if ($('#dialog-diff').length === 0) {
		createDiffDialog($('#cfg'), this.id, dialogOpts);
	} else if (!$('#dialog-diff').dialog("isOpen")) {
		$('#dialog-diff').dialog("open");
	}

	if (VISUAL.diffCnt%2 === 0) {
		fillDiffSelects('L', getAllNodesName(), node, getStatesName(getNode(node)), state);
		fillDiffContent('L', node, state);
	} else {
		fillDiffSelects('R', getAllNodesName(), node, getStatesName(getNode(node)), state);
		fillDiffContent('R', node, state);
	}
	VISUAL.diffCnt++;
}


/**
 * clear content of dialog.
 * @param e
 */
function hResetNode(e) {
	id = this.id	// 'dialg-#'
	$('#'+id+'-content').children().remove();
	$('#'+id+'-select1')[0].selectedIndex = 0
	$('#'+id+'-select2')[0].selectedIndex = 0
	$('#ui-id-'+id.split("-")[1])[0].innerText = "Node";			// change dialog title.
}

/**
 * change elements of State Select-box. 
 * @param e value of selected element (dialog num:nodename:select value)
 */
function hNodeSelect1Changed(e) {
	// e = > dialog num:nodename:select value
	var values = e.split(":");
	var dialognum = values[0];	// #
	var selectValue = values[2];	// selected value

	$('#dialog-'+dialognum+'-select2').children().remove();	// clear content
	$('#dialog-'+dialognum+'-content').children().remove();	// clear content

	if (selectValue !== "default") {
		var sb = "";
		var statesnames = getStatesName(getNode(selectValue));
		$('#ui-id-'+dialognum)[0].innerText = selectValue;			// change dialog title.

		sb += "<option value=" + dialognum + ":" + selectValue + ":" + "default" + " selected=\"true\">" + "State" + "</option>";
		for (var i=0 ; i<statesnames.length ; i++) {
			sb += "<option value=" + dialognum + ":" + selectValue + ":" + statesnames[i] + ">" + statesnames[i] + "</option>";
		}
		$('#dialog-'+dialognum+'-select2').append(sb);			// chagnge state selection element.
	}
}

/**
 * change heap list applied folding.
 * @param e value of selected element
 */
function hNodeSelect2Changed(e) {
	// e = > dialog num:nodename:select value
	var values = e.split(":");
	var dialognum = values[0];	// #
	var nodename = values[1];	// node name
	var selectValue = values[2];	// selected value
	
	$('#dialog-'+dialognum+'-content').children().remove();	// clear content

	if (selectValue !== "default") {
		$('#dialog-'+dialognum+'-content').append(getNode(nodename).getState(selectValue).toTag(dialognum));
	}
	for (i=0 ; i< $('#dialog-'+dialognum+'-content a').length ; i++)
		applyToolTip(nodename, selectValue, $('#dialog-'+dialognum+'-content a')[i]);

	applyFolding(dialognum);
	$(function() {
		$('#dialog-'+dialognum+'-content a').tooltip({
			track: true,
			delay: 0,
			showURL: false,
			top: 10,
			left: 10
		})
	});
}


/**
 * create dialog tag has only div child.
 * @param node node object
 * @param parent parent element for this doalog.
 * @param id dialog number
 * @param name node name
 * @param dialogOpts css option for this dialog.
 */
function createSingleDialog(parent, id, name, dialogOpts) {
	_id = "dialog-"+id;
	var dialog = "<div id='" + _id + "'></div>";
	$(parent).append(dialog);
	$('#'+_id).dialog(dialogOpts);
	
	var select = createSelect(name, id, _id + "-select1", getAllNodesName(), "Node", "hNodeSelect1Changed");
	$('#'+_id).append(select);
	$('#'+_id+'-select1')[0].style.marginLeft = "5px";
	
	select = createSelect(name, id, _id + "-select2", getStatesName(getNode(name)), "State", "hNodeSelect2Changed");
	$('#'+_id).append(select);
	$('#'+_id+'-select2')[0].style.marginLeft = "10%";
		
	$('#'+_id).append("</br></br>");
		
	content = "<div id='" + _id + "-content' class=\"ui-widget ui-widget-content ui-corner-all\"></div>";
	$('#'+_id).append(content);
}

/**
 * create dialog when CFG node was clicked.
 * @param e
 */
function hNodeClick(e) {
	var curNode = this.parentNode.childNodes[2];
	if (typeof prvNode != 'undefined')
		$(prvNode).css({'fill':''});
	$(curNode).css({'fill':'#CCFFCC'});	// change node color
	prvNode = curNode;
	VISUAL.clkCnt++;
	
	var name = this.parentNode.childNodes[4].textContent;
	var node = getNode(name);
	var dialogOpts = {
			title: name,
			autoOpen: true,
			closeOnEscape: true,
			closeText: "x",
			position: [e.clientX, e.clientY],
			show: "blind",
			hide: "explode",
			width : "288",
			height : "360",
			stack:true,
			resize:true,
			buttons:{"move to Diff":hMoveToDiff, "reset":hResetNode}
		};
	createSingleDialog(document.getElementById("cfg"), VISUAL.clkCnt, name, dialogOpts);
}

function checkUrlParam() {
	var href = decodeURI(window.location.href);
	if (href.split("?").length>1) {
		var params = href.split("?")[1].split("&");
		VISUAL.selectedNode = params[0];
		VISUAL.selectedStr = params[1];
	} else {
		VISUAL.selectedNode = "";
		VISUAL.selectedStr = "";
	}
	
}

function highlighSelectingInst() {
	var texts = $("text");
	for(var i=0 ; i<texts.length ; i++) {
		if (texts[i].textContent === VISUAL.selectedStr) {
			$(texts[i]).css({'fill':'#ff8c00', 'font-weight':'bold'});
			$(texts[i]).focus();
			return;
		}
	}
}

//init
(function () {
	checkUrlParam();
	VISUAL.nodes = parsingHeap();
	VISUAL.fid = fid;
	VISUAL.clkCnt=0;
	VISUAL.diffCnt=0;

	highlighSelectingInst();
	$("text").click(hNodeClick);
})();
