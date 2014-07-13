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
// H e l p e r   f u n c t i o n s   f o r   s o r t s
// -----------------------------------------------------------------------------
var randomInitialSeed = 74755; 
var randomSeed;
function random() {
  randomSeed = ((randomSeed * 1309) + 13849) % 65536;
  return randomSeed;
}

function SortData(length) {
  randomSeed = randomInitialSeed;
  var array = new Array(length);
  for (var i = 0; i < length; i++) array[i] = random();

  var min, max; 
  min = max = array[0];
  for (var i = 0; i < length; i++) {
    var e = array[i];
    if (e > max) max = e;
    if (e < min) min = e;
  }

  this.min = min;
  this.max = max;
  this.array = array;
  this.length = length;
}

function check(data) {
  var a = data.array;
  var len = data.length;
  if ((a[0] != data.min) || (a[len - 1] != data.max))
    error("Array is not sorted");
  for (var i = 1; i < len; i++) {
    if (a[i - 1] > a[i]) error("Array is not sorted");
  }
}


// -----------------------------------------------------------------------------
// B u b b l e S o r t
// -----------------------------------------------------------------------------
function doBubblesort(a, len) {
  for (var i = len - 2; i >= 0; i--) {
    for (var j = 0; j <= i; j++) {
      var c = a[j], n = a[j + 1];
      if (c > n) {
        a[j] = n;
        a[j + 1] = c;
      }
    }
  }
}

function bubblesort() {
  var data = new SortData(130);
  doBubblesort(data.array, data.length);
  check(data);
}

var BubbleSort = new Benchmark("BubbleSort", bubblesort);


// -----------------------------------------------------------------------------
// Q u i c k S o r t
// -----------------------------------------------------------------------------
function doQuicksort(a, low, high) {
  var pivot = a[(low + high) >> 1];
  var i = low, j = high;
  while (i <= j) {
    while (a[i] < pivot) i++;
    while (pivot < a[j]) j--;
    if (i <= j) {
      var tmp = a[i];
      a[i] = a[j];
      a[j] = tmp;
      i++;
      j--;
    }
  }

  if (low < j) doQuicksort(a, low, j);
  if (i < high) doQuicksort(a, i, high);
}

function quicksort() {
  var data = new SortData(800);
  doQuicksort(data.array, 0, data.length - 1);
  check(data);
}

var QuickSort = new Benchmark("QuickSort", quicksort);



// -----------------------------------------------------------------------------
// T r e e S o r t
// -----------------------------------------------------------------------------
function TreeNode(value) {
  this.value = value;
  this.left = null;
  this.right = null;
}

TreeNode.prototype.insert = function (n) {
  if (n < this.value) {
    if (this.left == null) this.left = new TreeNode(n);
    else this.left.insert(n);
  } else {
    if (this.right == null) this.right = new TreeNode(n);
    else this.right.insert(n);
  }
};

TreeNode.prototype.check = function () {
  var left = this.left, right = this.right, value = this.value;
  return ((left == null)  || ((left.value  <  value) && left.check())) &&
         ((right == null) || ((right.value >= value) && right.check()));
};

function treesort() {
  var data = new SortData(1000);
  var a = data.array;
  var len = data.length;
  var tree = new TreeNode(a[0]);
  for (var i = 1; i < len; i++) tree.insert(a[i]);
  if (!tree.check()) error("Invalid result, tree not sorted");
}

var TreeSort = new Benchmark("TreeSort", treesort);


// -----------------------------------------------------------------------------
// M a i n 
// -----------------------------------------------------------------------------
time(BubbleSort);

var logMean = 0;
for (var i = 0; i < allResults.length; i++)
  logMean += Math.log(allResults[i]);
logMean /= allResults.length;

//print("Geometric mean: " + Math.round(Math.pow(Math.E, logMean)) + " us.");
