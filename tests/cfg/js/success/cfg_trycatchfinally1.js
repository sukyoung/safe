var x = 1;
try { 
  x = 2;
} catch (x) {
  x = 4;
  try {
    var temp = "hi";
  } finally {}
} finally {
  x = 8;
}
x = 16;

