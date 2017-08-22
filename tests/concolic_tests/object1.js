function Packet(id, kind) {
	this.id = id;
	this.kind = kind;
}

var KIND_WORKER = 0;
var KIND_STUDENT = 1;
var KIND_PROFESSOR = 2;

function queue(packet) {
	if (packet.id) {
		//_<>_print("id: "+packet.id);
		//_<>_print("kind: "+packet.kind);
	}
	return packet.id;
}

function test(packet) {
	if (packet.kind === KIND_WORKER) {
		//_<>_print("worker id: "+packet.id);
	}
}

var p = new Packet(20100484, KIND_STUDENT);
if (queue(p)) 
	test(p);
//queue(p);
