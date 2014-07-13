
Array.prototype.unique = function() {
	var a = {};
	for (var i=0 ; i<this.length ; i++) {
		a[this[i].text] = this[i];
	}
	this.length = 0;
	for (var i in a) {
		this[this.length] = {"text":a[i].text, "tag":a[i].tag, "obj":a[i].obj};
	}
	return this;
}

Array.prototype.contains = function(obj) {
    var i = this.length;
    while (i--) {
        if (this[i] === obj) {
            return true;
        }
    }
    return false;
}

Array.prototype.remove = function(idx) {
	return (idx<0 || idx>this.length) ? this : this.slice(0, idx).concat(this.slice(idx+1, this.length));
}

String.prototype.insert = function(idx, str) {
	return this.substr(0, idx) + str + this.substr(idx)
}

function comparefiled(a,b) {
	var fa, fb;
	if (a.name === undefined) {
		fa = a.text;	fb = b.text;
	} else {
		fa = a.name;	fb = b.name;
	}

	if (fa[0] === "@" && fb[0] !== "@")	return -1;
	else if (fa[0] !== "@" && fb[0] === "@")	return 1;

	if (!isNaN(fa*1) && isNaN(fb*1))	return 1;
	else if (isNaN(fa*1) && !isNaN(fb*1))		return -1;

	if (fa < fb)		return -1;
	if (fa > fb)		return 1;
	return 0;
	
}

function diffOffset(o, n, arrow) {
	o = o.replace(/\s+$/, '');
	n = n.replace(/\s+$/, '');

	var del = [];
	var out = diff(o == "" ? [] : o.split(/\s+/), n == "" ? [] : n.split(/\s+/) );
	var str = "";

	var oSpace = o.match(/\s+/g);
	if (oSpace == null) {
		oSpace = ["\n"];
	} else {
		oSpace.push("\n");
	}
	var nSpace = n.match(/\s+/g);
	if (nSpace == null) {
		nSpace = ["\n"];
	} else {
		nSpace.push("\n");
	}

	var offset = 0;

    for ( var i = 0; i < out.n.length; i++ ) {
    	if (out.n[i].text == null) {
    		if (out.n[i] === "<a>" || out.n[i] === "</a>") {
    			offset += out.n[i].length + nSpace[i].length;
    		} else {
    			del.push(offset + ":" + (offset+out.n[i].length));
    			offset += out.n[i].length + nSpace[i].length;
    		}
    	} else {
    		offset += out.n[i].text.length + nSpace[i].length;
    	}
    }
    if (arrow && del.length === out.n.length-1)
    	return false;
    return del;
}
  	
function diff( o, n ) {
  var ns = new Object();
  var os = new Object();
  
  for ( var i = 0; i < n.length; i++ ) {
    if ( ns[ n[i] ] == null )
      ns[ n[i] ] = { rows: new Array(), o: null };
    ns[ n[i] ].rows.push( i );
  }
  
  for ( var i = 0; i < o.length; i++ ) {
    if ( os[ o[i] ] == null )
      os[ o[i] ] = { rows: new Array(), n: null };
    os[ o[i] ].rows.push( i );
  }
  
  for ( var i in ns ) {
    if ( ns[i].rows.length == 1 && typeof(os[i]) != "undefined" && os[i].rows.length == 1 ) {
      n[ ns[i].rows[0] ] = { text: n[ ns[i].rows[0] ], row: os[i].rows[0] };
      o[ os[i].rows[0] ] = { text: o[ os[i].rows[0] ], row: ns[i].rows[0] };
    }
  }
  
  for ( var i = 0; i < n.length - 1; i++ ) {
    if ( n[i].text != null && n[i+1].text == null && n[i].row + 1 < o.length && o[ n[i].row + 1 ].text == null && 
         n[i+1] == o[ n[i].row + 1 ] ) {
      n[i+1] = { text: n[i+1], row: n[i].row + 1 };
      o[n[i].row+1] = { text: o[n[i].row+1], row: i + 1 };
    }
  }
  
  for ( var i = n.length - 1; i > 0; i-- ) {
    if ( n[i].text != null && n[i-1].text == null && n[i].row > 0 && o[ n[i].row - 1 ].text == null && 
         n[i-1] == o[ n[i].row - 1 ] ) {
      n[i-1] = { text: n[i-1], row: n[i].row - 1 };
      o[n[i].row-1] = { text: o[n[i].row-1], row: i - 1 };
    }
  }
  
  return { o: o, n: n };
}

function Diffs() {
	this.diffs = [];
	this.isEmpty = function() {
		if (this.diffs.length==0)
			return true;
		return false;
	}
	this.push = function(diff) {
		this.diffs.push(diff);
	}
	this.append = function(arr) {
		this.diffs = this.diffs.concat(arr);
		return this;
	}
	this.remove = function(idx) {
		this.diffs.remove(idx);
	}
	this.popObj = function(obj) {
		var ret = new Diffs();
		for (var i=0 ; i<this.getLength() ; i++) {
			if (this.diffs[i].obj === obj) {
				ret.push(this.diffs[i]);
				this.remove(i);
			}
		}
		return ret;
	}
	this.getLength = function() {
		return this.diffs.length;
	}
}

function Diff(obj, tag, diffSpan, missLine) {
	this.obj = obj;
	this.tag = tag;
	this.diffSpan = diffSpan;
	this.missLine = missLine;
}

function propCmp(stand, diffed, id) {
	var diffs = new Diffs();
	var s=0, d=0;
	while(s<stand.length && d<diffed.length) {
		if (stand[s].tag === diffed[d].tag) {
			diffs.push(new Diff(diffed[d].obj, "equal"));
			s++; d++;
		} else {
			var spans = diffOffset(stand[s].tag, diffed[d].tag, true)
			if (spans === false) {
				diffs.push(new Diff(diffed[d].obj, "miss"));
				s++;
			} else {
				diffs.push(new Diff(diffed[d].obj, "diff", spans));
				s++; d++;
			}
		}
	}

	while (s<stand.length) {
		diffs.push(new Diff(diffed[d-1].obj, "miss"));
		s++
	}
	while (d<diffed.length) {
		diffs.push(new Diff(diffed[d].obj, "insert"));
		d++
	}

	return diffs;
}

function locCmp(stand, diffed, id) {
	var diffs = new Diffs();
	var s=0, d=0;
	while(s<stand.length && d<diffed.length) {
		if (stand[s].text === diffed[d].text) {
			// check prop field
			var _stand=[], _diffed=[];
			for (var i=0 ; i<stand[s].obj.propLength ; i++) {
				_stand.push({"text":$(stand[s].obj.props[i].toTag(id)).text().replace(" ",""), "tag":stand[s].obj.props[i].toTag(id), "obj":stand[s].obj.props[i]});
			}
			for (var i=0 ; i<diffed[d].obj.propLength ; i++) {
				_diffed.push({"text":$(diffed[d].obj.props[i].toTag(id)).text().replace(" ",""), "tag":diffed[d].obj.props[i].toTag(id), "obj":diffed[d].obj.props[i]});
			}
			var ds = propCmp(_stand.sort(comparefiled), _diffed.sort(comparefiled), id);
			var flag = true;
			for (var i=0 ; i<ds.getLength() ; i++) {
				if (ds.diffs[i].tag !== "equal") {
					flag = false;	break;
				}
			}
			if (flag){
				diffs.push(new Diff(diffed[d].obj, "equal"));
			} else {
				diffs.push(new Diff(diffed[d].obj, "diff"));
				diffs.append(ds.diffs);
			}
			s++; d++;
		} else {
			diffs.push(new Diff(diffed[d].obj, "miss", stand[s].obj.propLength+1));
			s++;
		}
	}

	while (s<stand.length) {
		diffs.push(new Diff(stand[s].obj, "miss", stand[s].obj.propLength+1));
		s++
	}
	while (d<diffed.length) {
		diffs.push(new Diff(diffed[d].obj, "insert"));
		d++
	}

	return diffs;
}

function contextDiff(ctx1, ctx2, id) {
	var diffs1 = new Diffs();
	var offs1 = diffOffset(ctx2.toTag(id), ctx1.toTag(id))
	if (offs1.length > 0) {
		diffs1.push(new Diff(ctx1, "diff", offs1));
	}
	
	var diffs2 = new Diffs();
	var offs2 = diffOffset(ctx1.toTag(id), ctx2.toTag(id))
	if (offs2.length > 0) {
		diffs2.push(new Diff(ctx2, "diff", offs2));
	}
	return [diffs1, diffs2];
}

function stateDiff(stat1, stat2, id) {
	var join = [];
	var objs1 = [];
	var objs2 = [];
	
	var ctxDiff = contextDiff(stat1.context, stat2.context, id)
	
	// collect locs
	for (var i=0 ; i<stat1.heap.length ; i++) {
		join.push({"text":stat1.heap.objs[i].name, "tag":stat1.heap.objs[i].toTag(id), "obj":stat1.heap.objs[i]});
		objs1.push({"text":stat1.heap.objs[i].name, "tag":stat1.heap.objs[i].toTag(id), "obj":stat1.heap.objs[i]});
	}
	for (var i=0 ; i<stat2.heap.length ; i++) {
		join.push({"text":stat2.heap.objs[i].name, "tag":stat2.heap.objs[i].toTag(id), "obj":stat2.heap.objs[i]});
	}
	
	// comp ctx1
	objs1.sort(comparefiled);
	join.unique(comparefiled);
	join.sort(comparefiled);
	
	var res1 = locCmp(join, objs1, id)
	
	// comp ctx2
	join = [];
	for (var i=0 ; i<stat2.heap.length ; i++) {
		objs2.push({"text":stat1.heap.objs[i].name, "tag":stat1.heap.objs[i].toTag(id), "obj":stat2.heap.objs[i]});
		join.push({"text":stat1.heap.objs[i].name, "tag":stat1.heap.objs[i].toTag(id), "obj":stat2.heap.objs[i]});
	}
	for (var i=0 ; i<stat1.heap.length ; i++) {
		join.push({"text":stat1.heap.objs[i].name, "tag":stat1.heap.objs[i].toTag(id), "obj":stat1.heap.objs[i]});		
	}
	objs2.sort(comparefiled);
	join.unique(comparefiled);
	join.sort(comparefiled);

	var res2 = locCmp(join, objs2, id);

	return [res1.append(ctxDiff[0].diffs),res2.append(ctxDiff[1].diffs)];
}






