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
//  print('Time (' + benchmark.string + '): ' + Math.floor(usec) + ' us.');
}

function error(string) {
  //print(string);
}


// -----------------------------------------------------------------------------
// L o o p
// -----------------------------------------------------------------------------
function loop() {
  var sum = 0;
  for (var i = 0; i < 200; i++) {
    for (var j = 0; j < 100; j++) {
      sum++;
    }
  }
  if (sum != 20000) error("Wrong result: " + sum + " should be: 20000");
}

var Loop = new Benchmark("Loop", loop);


// -----------------------------------------------------------------------------
// M a i n 
// -----------------------------------------------------------------------------
time(Loop);

var logMean = 0;
for (var i = 0; i < allResults.length; i++)
  logMean += Math.log(allResults[i]);
logMean /= allResults.length;

//print("Geometric mean: " + Math.round(Math.pow(Math.E, logMean)) + " us.");
