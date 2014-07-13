// Benchpress: A collection of micro-benchmarks.

var allResults = [ ];


// -----------------------------------------------------------------------------
// F r a m e w o r k
// -----------------------------------------------------------------------------
function Benchmark(string, run) {
  this.string = string;
  this.run = run;
}

// Run each benchmark for two seconds and count number of iterations.
function time(benchmark) {
  var elapsed = 0;
  var start = new Date();
  for (var n = 0; elapsed < 2000; n++) {
    benchmark.run();
    elapsed = new Date() - start;
  }
  var usec = (elapsed * 1000) / n;
  allResults.push(usec);
  //print('Time (' + benchmark.string + '): ' + Math.floor(usec) + ' us.');
}

function error(string) {
  //print(string);
}



// -----------------------------------------------------------------------------
//  T a k
// -----------------------------------------------------------------------------
function tak(x, y, z) {
 if (y >= x) return z;    
 return tak(tak(x-1, y, z), tak(y-1, z, x), tak(z-1, x, y));   
}

var Tak = new Benchmark("Tak", function () { tak(18,12,6); });


// -----------------------------------------------------------------------------
// M a i n 
// -----------------------------------------------------------------------------
time(Tak);

var logMean = 0;
for (var i = 0; i < allResults.length; i++)
  logMean += Math.log(allResults[i]);
logMean /= allResults.length;

//print("Geometric mean: " + Math.round(Math.pow(Math.E, logMean)) + " us.");
