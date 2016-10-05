  var c1 = 0;
  try
{    c1 += 1;
    y;
    $ERROR('#1.1: "y" lead to throwing exception');}
  catch (e)
{    c1 *= 2;}

  {
    var __result1 = c1 !== 2;
    var __expect1 = false;
  }
  var c2 = 0;
  try
{    c2 += 1;}
  finally
{    c2 *= 2;}

  {
    var __result2 = c2 !== 2;
    var __expect2 = false;
  }
  var c3 = 0;
  try
{    c3 = 1;
    z;}
  catch (err)
{    c3 *= 2;}

  finally
{    c3 += 1;}

  {
    var __result3 = c3 !== 3;
    var __expect3 = false;
  }
  var c4 = 0;
  try
{    c4 = 1;}
  catch (err)
{    c4 *= 3;}

  finally
{    c4 += 1;}

  {
    var __result4 = c4 !== 2;
    var __expect4 = false;
  }
  