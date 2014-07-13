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
// F i b o n a c c i
// -----------------------------------------------------------------------------
function doFib(n) {
  if (n <= 1) return 1;
  return doFib(n - 1) + doFib(n - 2);
}

function fib() {
  var result = doFib(20);
  if (result != 10946) error("Wrong result: " + result + " should be: 10946");
}

var Fibonacci = new Benchmark("Fibonacci", fib); 



// -----------------------------------------------------------------------------
// M a i n 
// -----------------------------------------------------------------------------
time(Fibonacci);

var logMean = 0;
for (var i = 0; i < allResults.length; i++)
  logMean += Math.log(allResults[i]);
logMean /= allResults.length;

//print("Geometric mean: " + Math.round(Math.pow(Math.E, logMean)) + " us.");
