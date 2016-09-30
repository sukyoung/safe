  var x = [];
  x.length = 4294967295;
  {
    var __result1 = x.length !== 4294967295;
    var __expect1 = false;
  }
  try
{    x = [];
    x.length = 4294967296;
    $ERROR('#2.1: x = []; x.length = 4294967296 throw RangeError. Actual: x.length === ' + (x.length));}
  catch (e)
{    {
      var __result2 = (e instanceof RangeError) !== true;
      var __expect2 = false;
    }}

  