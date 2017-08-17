function ack(m,n){
   if (m==0) { return n+1; }
   if (n==0) {
    var i = ack(m-1,1);
    return i;
  }
   var a = ack(m,n-1);
   var b = ack(m-1, a );
   return b;
}

function fib(n) {
    if (n < 2){ return 1; }
    var c = fib(n-2);
    var d = fib(n-1);
    return c + d;
}

function tak(x,y,z) {
    if (y >= x) return z;
    var e = tak(x-1,y,z);
    var f = tak(y-1,z,x);
    var g = tak(z-1,x,y);
    var h = tak(e, f, g);
    return h;
}

for ( var i = 3; i <= 5; i++ ) {
    ack(3,i);
    fib(17.0+i);
    tak(3*i+3,2*i+2,i+1);
}
