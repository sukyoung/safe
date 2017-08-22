function f(a, b) {
	if (a != null) 
		b = b + 3
	else
		b = b - 3
}
function Packet(id) {this.id = id}

f(new Packet(1), 3)

