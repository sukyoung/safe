  var x = new Array();
  if (x.toString() !== x.join())
  {
    $ERROR('#1.1: x = new Array(); x.toString() === x.join(). Actual: ' + (x.toString()));
  }
  else
  {
    {
      var __result1 = x.toString() !== "";
      var __expect1 = false;
    }
  }
  x = [];
  x[0] = 1;
  x.length = 0;
  if (x.toString() !== x.join())
  {
    $ERROR('#2.1: x = []; x[0] = 1; x.length = 0; x.toString() === x.join(). Actual: ' + (x.toString()));
  }
  else
  {
    {
      var __result2 = x.toString() !== "";
      var __expect2 = false;
    }
  }
  