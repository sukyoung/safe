  try
{    throw "catchme";
    $ERROR('#1: throw "catchme" lead to throwing exception');}
  catch (e)
{    }

  var c2 = 0;
  try
{    try
{      throw "exc";
      $ERROR('#2.1: throw "exc" lead to throwing exception');}
    finally
{      c2 = 1;}
}
  catch (e)
{    {
      var __result1 = c2 !== 1;
      var __expect1 = false;
    }}

  var c3 = 0;
  try
{    throw "exc";
    $ERROR('#3.1: throw "exc" lead to throwing exception');}
  catch (err)
{    var x3 = 1;}

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
  