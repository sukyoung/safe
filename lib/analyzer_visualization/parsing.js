function ObjValue(objValue) {
	this.configurable = objValue[0]["configurable"];
	this.enumerable = objValue[1]["enumerable"];
	this.value = new Value(objValue[2]["value"]);
	this.writable = objValue[3]["writable"];
	
	this.isDiff = function() {
		if (this.configurable !== that.configurable ||
				this.enumerable !== that.enumerable ||
				this.writable !== that.writable ||
				this.value.isDiff(that.value))
			return true;

		return false;
	};
	
	this.toToolTip = function() {
		var sb = "";
		sb += " ["+ this.writable + this.enumerable + this.configurable + "]  " + this.value.toToolTip();
		return sb;
	}

	this.toTag = function(id) {
		var sb = "";
		sb += " ["+ this.writable + this.enumerable + this.configurable + "]  " + this.value.toTag(id);
		return sb;
	};
}

function Value(value) {
	this.pvalue = value[1]["pvalue"];
	this.locLength = value[0]["locs"].length;
	this.locs = [];
	
	if (value[0]["locs"] != "Bot") {
		for (var i=0 ; i<value[0]["locs"].length ; i++) {
			this.locs[i] = value[0]["locs"][i];
		}
	}

	this.isDiff = function(that) {
		if (this.pvalue !== that.pvalue || this.locLength !== that.locLength) {
			return true;
		}
		
		for (var i=0 ; i<this.locLength ; i++) {
			if (this.locs[i].isDiff(that.locs[i]))
				return true;
		}
		return false;
	};
	
	this.toToolTip = function() {
		var sb = "";
		var flag = false;

		if (this.pvalue != "Bot") {
			sb += this.pvalue;
			flag = true;
		}

		for (var i=0 ; i<this.locs.length ; i++) {
			if (flag) sb += " , ";
			sb += " " + this.locs[i] + " ";
		}
		return sb;
	}

	this.toTag = function(id) {
		var sb = "";
		var flag = false;

		if (this.pvalue != "Bot") {
			sb += this.pvalue;
			flag = true;
		}

		for (var i=0 ; i<this.locs.length ; i++) {
			if (flag) sb += " ";
			sb += " <a> " + this.locs[i] + " </a> ";
		}
		return sb;
	};
}

function PropValue(propValue) {
	this.fid = propValue[0]["fid"];
	this.objValue = "Bot";
	this.value = "Bot";
	
	if (propValue[1]["objValue"] != "Bot") {
		this.objValue = new ObjValue(propValue[1]["objValue"]);
	}
	if (propValue[2]["value"] != "Bot") {
		this.value = new Value(propValue[2]["value"]);
	}
	
	this.isDiff = function(that) {
		if (this.fid !== that.fid) {
			return true;
		}
		
		if (this.objValue !== "Bot" && that.objValue !== "Bot") {
			if (this.objValue.isDiff(that.objValue))
				return true;
		} else if (this.objValue === "Bot" && that.objValue === "Bot"){
			// continue
		} else {
			return true;
		}

		if (this.value !== "Bot" && that.value !=="Bot") {
			if (this.value.isDiff(that.value))
				return true;
		} else if (this.value === "Bot" && that.value === "Bot") {
			// continue
		} else {
			return true;
		}
		return false;
	};
	
	this.toToolTip = function() {
		var sb = "";

		if (this.fid != "Bot") {
			sb += " [fid] " + this.fid;
		} else {
			if (this.objValue != "Bot") {
				sb += this.objValue.toToolTip();
			} else {
				if (this.value != "Bot") {
					sb += " [VAL] " + this.value.toToolTip();
				} else {
					sb += " Bot ";
				}
			}
		}

		return sb;
	}
	
	this.toTag = function(id) {
		var sb = "";

		if (this.fid != "Bot") {
			sb += " [fid] " + this.fid;
		} else {
			if (this.objValue != "Bot") {
				sb += this.objValue.toTag(id);
			} else {
				if (this.value != "Bot") {
					sb += " [VAL] " + this.value.toTag(id);
				} else {
					sb += " Bot ";
				}
			}
		}

		return sb;
	};	
}

function Prop(prop) {
	this.name = prop["name"];
	this.absent = prop["value"][0]["absent"];
	this.propValue = new PropValue(prop["value"][1]["propValue"]);
	
	this.isDiff = function(that) {
		if (this.name !== that.name || that.absent !== that.absent || this.propValue !== that.propValue) {
			return true;
		}
		return false;
	};
	
	this.toToolTip = function() {
		var sb = "";

		sb += " <li> ";
		sb += this.name + " -> "
		if (this.absent) {
			sb += " Absent ";
		} else {
			sb += this.propValue.toToolTip();
		}
		sb += " </li> ";

		return sb;
	}
	
	this.toTag = function(id) {
		var sb = "";

		sb += " <li> ";
		sb += this.name + " -> "
		if (this.absent) {
			sb += " Absent ";
		} else {
			sb += this.propValue.toTag(id);
		}
		sb += " </li> ";

		return sb;
	};
	
	this.toDiffTag = function(id, diffs) {
		var sb = "";
		var matched = diffs.popObj(this)
		
		if (matched.isEmpty()) {
			sb += " <li> ";
			sb += this.name + " -> "
			if (this.absent) {
				sb += " Absent ";
			} else {
				sb += this.propValue.toTag(id, diffs);
			}
			sb += " </li> ";
		} else {
			for (var i=0 ; i<matched.getLength() ; i++) {
				var tag = matched.diffs[i].tag;
				if (tag === "equal" || tag === "insert"){
					sb += " <li> ";
					sb += this.name + " -> "
					if (this.absent) {
						sb += " Absent ";
					} else {
						sb += this.propValue.toTag(id);
					}
					sb += " </li> ";
				} else if (tag === "miss") {
					sb += " <li style=\"background-color:yellow;\"></li> "
				} else if (tag === "diff") {
					var tags = " <span style=\"background-color:yellow\"> ";
					var tage = " </span> ";
					var s = matched.diffs[i].obj.toTag(id);
					var spans = matched.diffs[i].diffSpan;
					var os = 0;
					for (var j=0 ; j<spans.length ; j++) {
						var inds = spans[j].split(":");
						s = s.insert(inds[0]*1+os, tags);
						os += tags.length;
						s = s.insert(inds[1]*1+os, tage);
						os += tage.length;
					}
					sb += s;
				}
			}
		}
		return sb;

	}
}

function Loc(loc) {
	this.name = loc["name"];
	this.propLength = loc["obj"].length;	
	this.props = [];

	for(var i=0 ; i<loc["obj"].length ; i++) {
		this.props[i] = new Prop(loc["obj"][i]);
	}
	this.props.sort(comparefiled);

	this.isDiff = function(that) {
		if (this.name !== that.name || this.propLength !== that.propLength) {
			return true;
		}
		for (var i=0 ; i<this.propLength ; i++) {
			if (this.props[i].isDiff(that.props[i]))
				return true;
		}
		return false;
	};
	
	this.toToolTip = function() {
		var sb = "";
		sb += this.name + " <ul id=\"loc\"> ";
		for (var i=0 ; i<this.propLength ; i++) {
			sb += this.props[i].toToolTip();
		}
		sb += " </ul> ";
		return sb;
	}
	
	this.toTag = function(id) {
		var sb = "";
		sb += " <ul id=\"loc\"> ";
		for (var i=0 ; i<this.propLength ; i++) {
			sb += this.props[i].toTag(id);
		}
		sb += " </ul> ";
		return sb;
	};
	
	this.toDiffTag = function(id, diffs) {
		var sb = "";
		sb += " <ul id=\"loc\" >";
		for (var i=0 ; i<this.propLength ; i++) {
			sb += this.props[i].toDiffTag(id, diffs);
		}
		sb += " </ul> ";
		return sb;
	}
}

function Context(context) {
	this.cc1 = context["_1"];
	this.cc2 = context["_2"];
	
	this.isDiff = function(that) {
		if (this.cc1 === that.cc1 && this.cc2 === this.cc2)
			return false;
		return true;
	};
	
	this.tooltip = function(id) {
		return "undefined";
	}
	
	this.toTag = function(id) {
		var sb = "";
		sb += " <a id=\"loclink\" title=\""+ this.tooltip(id) + "\"> "+ this.cc1 + " </a> ";
		sb += " X ";
		sb += " <a id=\"loclink\" title=\""+ this.tooltip(id) + "\"> "+ this.cc2 + " </a> ";
		return sb;
	};
	
	this.toDiffTag = function(id, diffs) {
		var sb = "";
		var tags = " <span style=\"background-color:yellow\"> ";
		var tage = " </span> ";
		var matched = diffs.popObj(this)
		if (matched.getLength() > 0) {
			for (var i=0 ; i<matched.getLength() ; i++) {
				var s = matched.diffs[i].obj.toTag(id);
				var spans = matched.diffs[i].diffSpan;
				var os = 0;
				for (var j=0 ; j<spans.length ; j++) {
					var inds = spans[j].split(":");
					s = s.insert(inds[0]*1+os, tags);
					os += tags.length;
					s = s.insert(inds[1]*1+os, tage);
					os += tage.length;
				}
				sb += s;				
			}
			
		} else {
			sb += this.toTag(id);
		}
		return sb;
	}
}

function Heap(heap) {
	this.length = heap.length;
	this.objs = [];

	for(var i=0 ; i<heap.length ; i++) {
		this.objs[i] = new Loc(heap[i]);
	}
	this.objs.sort(comparefiled);
	
	this.isDiff = function(that) {
		if (this.length != that.length) {
			return true;
		}
		for(var i=0 ; i<this.length ; i++) {
			if (this.objs[i].isDiff(that.objs[i]))
				return true;
		}
		return false;
	};
	
	this.toTag = function(id) {
		var sb = "";
		sb += " <ul id=\"heap\"> ";
		for (var i=0 ; i<this.length ; i++) {
			var idName = this.objs[i].name.split("#")[1];
			if (idName==="#")	idName = this.objs[i].name.split("#")[2];
			sb += " <li class=\"folding"+id+"\" id=\""+id+idName+"\"> " + this.objs[i].name + " ";
			sb += this.objs[i].toTag(id);
			sb += " </li> ";
		}
		sb += "</ul>";
		return sb;
	};
	
	this.toDiffTag = function(id, diffs) {
		var sb = "";
		sb += "<ul id=\"heap\">";
	
		for (var i=0 ; i<this.length ; i++) {
			var idName = this.objs[i].name.split("#")[1];
			if (idName==="#")	idName = this.objs[i].name.split("#")[2];

			var matched = diffs.popObj(this.objs[i])
			if (matched.getLength() === 1 && matched.diffs[0].tag === "equal") {
//				sb += " <li class=\"folding"+id+"\" id=\""+id+idName+"\"> " + this.objs[i].name + " ";
//				sb += this.objs[i].toTag(id);
//				sb += " </li> ";
			} else {
				var emptyli = "<li style=\"background-color:yellow;\"></li>";
				for (var j=0 ; j<matched.getLength() ; j++) {
					var tag = matched.diffs[j].tag;
					if (tag === "miss") {
						for (var m=0 ; m<matched.diffs[j].missLine ; m++)
							sb += emptyli;
					} else if (tag === "diff") {
						sb += "<li class=\"folding"+id+"\" id=\""+id+idName+"\">";
						sb += "<span style=\"background-color:yellow\">";
						sb += this.objs[i].name;
						sb += "</span>";
						sb += this.objs[i].toDiffTag(id, diffs);
						sb += "</li>";
					}
				}
			}
		}
		sb += "</ul>";
		return sb;
	}
}

function State(state) {
	this.cc = state["0"]["cc"];
	this.context = new Context(state["1"]["context"]);
	this.heap = new Heap(state["2"]["heap"]);
	
	this.isDiff = function(that) {
		if (this.cc != that.cc || this.context.isDiff(that.context) || this.heap.isDiff(that.heap))
			return true;
		return false;
	};
	
	this.toTag = function(id) {
		var sb = "";
		
		sb += " <ul id=\"cc\"> ";
		sb += " <li> " + " Context : " + this.context.toTag(id) + " </li> ";
		sb += " <li class=\"folding"+id+"\"> " + " Heap ";
		sb += this.heap.toTag(id);
		sb += "</li> ";
		sb += " </ul> ";
		
		return sb;
	};
	
	this.toDiffTag = function(id, diffs) {
		var sb = "";
		
		sb += " <ul id=\"cc\"> ";
		sb += " <li> " + " Context : " + this.context.toDiffTag(id, diffs) + " </li> ";
		sb += " <li class=\"folding"+id+"\"> " + " Heap ";
		sb += this.heap.toDiffTag(id, diffs);
		sb += " </li> ";
		sb += " </ul> ";
		
		return sb;
	}
}

function Node(node) {
	this.name = node["name"];
	this.stateLen = 0;
	this.states = [];
	
	if (typeof(node["state"]) != "string") {
		this.stateLen = node["state"].length;
		for(var i=0 ; i<this.stateLen ; i++) {
			this.states[i] = new State(node["state"][i]);
		}
	}
	
	this.getState = function(id) {
		for (var i=0 ; i<this.stateLen ; i++) {
			if (this.states[i].cc === id)
				return this.states[i];
		}
		return undefined;
	};
	
	this.isDiff = function(that) {
		if (this.name != that.name || this.stateLen != that.stateLen) {
			return true;
		}
		for (var i=0 ; i<this.stateLen ; i++) {
			if (this.states[i].isDiff(that.states[i]))
				return true;
		}
		return false;
	};

	this.toTag = function(id) {
		var sb = "";
		sb += " <ul id=\"node\" > ";
		for (var i=0 ; i<this.stateLen ; i++) {
			sb += " <li class=\"folding"+id+"\"> "+ " cc : " + this.states[i].cc + " ";
			sb += this.states[i].toTag(id);
			sb += " </li> ";
		}
		sb += " </ul> ";
		return sb;
	};
	
	this.toDiffTag = function(id, diffs) {
		var sb = "";
		sb += " <ul id=\"node\"> ";
		for (var i=0 ; i<this.stateLen ; i++) {
			sb += " <li class=\"folding"+id+"\"> "+ " cc : " + this.states[i].cc + " ";
			sb += this.states[i].toDiffTag(id, diffs);
			sb += " </li> ";
		}
		sb += " </ul> ";
		return sb;
	}
}

function compareFid(a,b) {
	if (a.fid < b.fid)		return -1;
	if (a.fid > b.fid)		return 1;
	return 0;
	
}

function Fid(fid2Map) {
	this.fid = fid2Map["fid"];
	this.name = fid2Map["name"];
}


function parsingHeap() {
	var i, nodes = [];
	for (i=0 ; i<names.length ; i++) {
		nodes[i] = new Node(eval(names[i]));
	}	
	return nodes;
}

function parsingFid() {
	var i, fids=[];
	for (i=0 ; i<fid2name.length ; i++) {
		fids[i] = new Fid(fid2name[i]);
	}
	fids.sort(compareFid);
	return fids;
}