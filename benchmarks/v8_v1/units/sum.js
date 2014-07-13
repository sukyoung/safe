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
// S u m
// -----------------------------------------------------------------------------
function doSum(start, end) {
  var sum = 0;
  for (var i = start; i <= end; i++) sum += i;
  return sum;
}

function sum() {
  var result = doSum(1, 10000);
  if (result != 50005000) error("Wrong result: " + result + " should be: 50005000");
}

var Sum = new Benchmark("Sum", sum);



// -----------------------------------------------------------------------------
// M a i n 
// -----------------------------------------------------------------------------
time(Sum);

var logMean = 0;
for (var i = 0; i < allResults.length; i++)
  logMean += Math.log(allResults[i]);
logMean /= allResults.length;

//print("Geometric mean: " + Math.round(Math.pow(Math.E, logMean)) + " us.");
