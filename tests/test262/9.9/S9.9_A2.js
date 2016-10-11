  try
{    null['foo'];
    $ERROR('#1.1: null[\'foo\'] throw TypeError. Actual: ' + (null['foo']));}
  catch (e)
{    {
      var __result1 = (e instanceof TypeError) !== true;
      var __expect1 = false;
    }}

  try
{    with (null)
      x = 2;
    $ERROR('#2.1: with(null) x = 2 must throw TypeError. Actual: x === . Actual: ' + (x));}
  catch (e)
{    {
      var __result2 = (e instanceof TypeError) !== true;
      var __expect2 = false;
    }}

  