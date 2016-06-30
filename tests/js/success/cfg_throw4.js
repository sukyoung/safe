var x;
x = "A";
while(true) {
    x = "B";
    throw "C";
    if (x == "B") {throw 3;;}
}
x = "E"
