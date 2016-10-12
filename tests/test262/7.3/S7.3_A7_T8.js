// TODO eval: string escape
  var y = 2;
  var z = 3;
  var x = y < z;
  {
    var __result1 = x !== true;
    var __expect1 = false;
  }
  x = 0;
  var y = 2;
  var z = 3;
  var x = y < z;
  {
    var __result2 = x !== true;
    var __expect2 = false;
  }

/*
  x = 0;
  var y = 2;
  var z = 3;
eval("\u2028var\u2028x\u2028=\u2028y\u2028<\u2028z\u2028");
  {
    var __result3 = x !== true;
    var __expect3 = false;
  }
  x = 0;
  var y = 2;
  var z = 3;
eval("\u2029var\u2029x\u2029=\u2029y\u2029<\u2029z\u2029");
  {
    var __result4 = x !== true;
    var __expect4 = false;
  }
  */
