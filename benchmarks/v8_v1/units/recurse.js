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
// R e c u r s e
// -----------------------------------------------------------------------------
function recurse(n) {
  if (n <= 0) return 1;
  recurse(n - 1);
  return recurse(n - 1);
}

var Recurse = new Benchmark("Recurse", function () { recurse(13); });

// -----------------------------------------------------------------------------
// M a i n 
// -----------------------------------------------------------------------------
time(Recurse);

var logMean = 0;
for (var i = 0; i < allResults.length; i++)
  logMean += Math.log(allResults[i]);
logMean /= allResults.length;

//print("Geometric mean: " + Math.round(Math.pow(Math.E, logMean)) + " us.");
