  var x = new Array(0, 1, 2, 3);
  if (x.toString() !== x.join())
  {
    $ERROR('#1.1: x = new Array(0,1,2,3); x.toString() === x.join(). Actual: ' + (x.toString()));
  }
  else
  {
    {
      var __result1 = x.toString() !== "0,1,2,3";
      var __expect1 = false;
    }
  }
  x = [];
  x[0] = 0;
  x[3] = 3;
  if (x.toString() !== x.join())
  {
    $ERROR('#2.1: x = []; x[0] = 0; x[3] = 3; x.toString() === x.join(). Actual: ' + (x.toString()));
  }
  else
  {
    {
      var __result2 = x.toString() !== "0,,,3";
      var __expect2 = false;
    }
  }
  x = Array(undefined, 1, null, 3);
  if (x.toString() !== x.join())
  {
    $ERROR('#3.1: x = Array(undefined,1,null,3); x.toString() === x.join(). Actual: ' + (x.toString()));
  }
  else
  {
    {
      var __result3 = x.toString() !== ",1,,3";
      var __expect3 = false;
    }
  }
  x = [];
  x[0] = 0;
  if (x.toString() !== x.join())
  {
    $ERROR('#4.1: x = []; x[0] = 0; x.toString() === x.join(). Actual: ' + (x.toString()));
  }
  else
  {
    {
      var __result4 = x.toString() !== "0";
      var __expect4 = false;
    }
  }
  