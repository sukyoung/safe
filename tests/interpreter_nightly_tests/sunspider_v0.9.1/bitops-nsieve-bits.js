//function record(time) {
//    document.getElementById("console").innerHTML = time + "ms";
//    if (window.parent) {
//        parent.recordResult(time);
//    }
//}

//var _sunSpiderStartDate = new Date();

// The Great Computer Language Shootout
//  http://shootout.alioth.debian.org
//
//  Contributed by Ian Osgood

function pad(n,width) {
  var s = n.toString();
  while (s.length < width) s = ' ' + s;
  return s;
}

function primes(isPrime, n) {
  var i, count = 0, m = 10000<<n, size = m+31>>5;

  for (i=0; i<size; i++) isPrime[i] = 0xffffffff;

  for (i=2; i<m; i++)
    if (isPrime[i>>5] & 1<<(i&31)) {
      for (var j=i+i; j<m; j+=i)
        isPrime[j>>5] &= ~(1<<(j&31));
      count++;
    }

  return count;
}

function sieve() {
    for (var i = 4; i <= 4; i++) {
        var isPrime = new Array((10000<<i)+31>>5);
        var ret = primes(isPrime, i);
        _<>_print(ret);
    }
}

sieve();


//var _sunSpiderInterval = new Date() - _sunSpiderStartDate;

//record(_sunSpiderInterval);
