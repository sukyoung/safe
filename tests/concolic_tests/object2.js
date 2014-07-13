function Packet(link, id, kind) {
	this.link = link;
	this.id = id;
	this.kind = kind;
	this.a1 = 0;
	this.a2 = new Array(4);
}

function f(p, q) {
	p.id = p.id - 1;
	if (p.id);
}

var p = new Packet(null, 1, "first packet");
var q = new Packet(null, 2, "second packet");
f(p, q);
