if (Math.random()) {
    len = 22
    cols = 21
} else {
    len = 47
    cols = 3
}

function Cell() {
//  return {}
	return {abc:"ABC"}  // for SAFE
}

o = {}
o.rows = new Array(len);
for (row = 0; row != len; ++ row) {
      o.rows[row] = new Array(cols);
      for (col = 0; col != cols; ++ col) {
        o.rows[row][col] = new Cell();
      }
}
//dumpValue(o)
var __result1 = o.rows[10][2].abc;  // for SAFE
var __expect1 = "ABC";  // for SAFE
