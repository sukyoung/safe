
var CG = {};

// init
(function () {
	CG.fids = parsingFid();
	CG.filename = filename;
	$("text").click(hFuncClick);
})();


function hFuncClick() {
	for (var i=0 ; i<CG.fids.length ; i++) {
		if (CG.fids[i].name === this.textContent) {
			location.href = "./"+"f"+CG.fids[i].fid+".html";
		}
	}
}
