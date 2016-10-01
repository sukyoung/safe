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
  try {
  } catch (x) {;;;x;;;;;;}
}
x = 16;

