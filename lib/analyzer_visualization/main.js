var MAIN = {};


function hSelectChange(e) {
	$('#frm')[0].src = e.value;
}

function hShowSource() {
	$('#frm')[0].src = "./source.html";
}

function hShowCallGraph() {
	$('#frm')[0].src = "./callgraph.html";
}

function hFrmLoad(src) {
	var names = src.split("/");
	var name = names[names.length-1].split(".")[0];

//	alert(name);
//	for (var i=0 ; i<MAIN.fids.length ; i++) {
//		if (MAIN.fids[i].name === name) {
//			$('#dropdown')[0].selectedIndex = i;
//		}
//	}
}

function hShowFid() {
	var sb = "";
	var fids = MAIN.fids;
	
	for (var i=0 ; i<MAIN.fids.length ; i++) {
		sb += fids[i].fid + " -> " + fids[i].name + "\n";
	}
	alert(sb);
}

function addSelectOption() {
	var sb ="";
	for (var i=0 ; i<MAIN.fids.length ; i++) {
		sb += "<option value=\"./"+ "f" + MAIN.fids[i].fid + ".html\">" + "[" + MAIN.fids[i].fid+ "] " +	MAIN.fids[i].name + "</option>";
	}
	return sb;
}

(function() {
	MAIN.fids = parsingFid();
	MAIN.filenemt = filename;
	$('#dropdown').append(addSelectOption());
})();

