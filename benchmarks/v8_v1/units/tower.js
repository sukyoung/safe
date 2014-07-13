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
// T o w e r s
// -----------------------------------------------------------------------------
var towersPiles, towersMovesDone;

function TowersDisk(size) {
  this.size = size;
  this.next = null;
}

function towersPush(pile, disk) {
  var top = towersPiles[pile];
  if ((top != null) && (disk.size >= top.size))
    error("Cannot put a big disk on a smaller disk");
  disk.next = top;
  towersPiles[pile] = disk;
}

function towersPop(pile) {
  var top = towersPiles[pile];
  if (top == null) error("Attempting to remove a disk from an empty pile");
  towersPiles[pile] = top.next;
  top.next = null;
  return top;
}

function towersMoveTop(f, to) {
  towersPush(to, towersPop(f));
  towersMovesDone++;
}

function towersMove(f, to, disks) {
  if (disks == 1) {
    towersMoveTop(f, to);
  } else {
    var other = 3 - f - to;
    towersMove(f, other, disks - 1);
    towersMoveTop(f, to);
    towersMove(other, to, disks - 1);
  }
}

function towersBuild(pile, disks) {
  for (var i = disks - 1; i >= 0; i--) {
    towersPush(pile, new TowersDisk(i));
  }
}

function towers() {
  towersPiles = [ null, null, null ];
  towersBuild(0, 13);
  towersMovesDone = 0;
  towersMove(0, 1, 13);
  if (towersMovesDone != 8191) 
    error("Error in result: " + towersMovesDone + " should be: 8191");
}

var Towers = new Benchmark("Towers", towers);


// -----------------------------------------------------------------------------
// M a i n 
// -----------------------------------------------------------------------------
time(Towers);

var logMean = 0;
for (var i = 0; i < allResults.length; i++)
  logMean += Math.log(allResults[i]);
logMean /= allResults.length;

//print("Geometric mean: " + Math.round(Math.pow(Math.E, logMean)) + " us.");
