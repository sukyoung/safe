var x,y;
var out = 123;
lab1: for(x=0; x<10; x++) {
    lab2: for(y=0; y<10; y++) {
        continue lab1;
    }
    out = 456;
}
out;
