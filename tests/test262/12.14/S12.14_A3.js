  try
{    y;
    $ERROR('#1: "y" lead to throwing exception');}
  catch (e)
{    }

  var c2 = 0;
  try
{    try
{      someValue;
      $ERROR('#3.1: "someValues" lead to throwing exception');}
    finally
{      c2 = 1;}
}
  catch (e)
{    {
      var __result1 = c2 !== 1;
      var __expect1 = false;
    }}

  var c3 = 0, x3 = 0;
  try
{    x3 = someValue;
    $ERROR('#3.1: "x3=someValues" lead to throwing exception');}
  catch (err)
{    x3 = 1;}

  finally
{    c3 = 1;}

  {
    var __result2 = x3 !== 1;
    var __expect2 = false;
  }
  {
    var __result3 = c3 !== 1;
    var __expect3 = false;
  }
  