  try
{    var x = 0;}
  catch (e)
{    $ERROR('#1: If Result(1).type is not throw, return Result(1). Actual: 4 Return(Result(3))');}

  var c1 = 0;
  try
{    var x1 = 1;}
  finally
{    c1 = 1;}

  {
    var __result1 = x1 !== 1;
    var __expect1 = false;
  }
  {
    var __result2 = c1 !== 1;
    var __expect2 = false;
  }
  var c2 = 0;
  try
{    var x2 = 1;}
  catch (e)
{    $ERROR('#3.1: If Result(1).type is not throw, return Result(1). Actual: 4 Return(Result(3))');}

  finally
{    c2 = 1;}

  {
    var __result3 = x2 !== 1;
    var __expect3 = false;
  }
  {
    var __result4 = c2 !== 1;
    var __expect4 = false;
  }
  