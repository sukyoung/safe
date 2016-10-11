  try
{    undefined['foo'];
    $ERROR('#1.1: undefined[\'foo\'] must throw TypeError. Actual: ' + (undefined['foo']));}
  catch (e)
{    {
      var __result1 = (e instanceof TypeError) !== true;
      var __expect1 = false;
    }}

  try
{    with (undefined)
      x = 2;
    $ERROR('#2.1: with(undefined) x = 2 must throw TypeError. Actual: x === ' + (x));}
  catch (e)
{    {
      var __result2 = (e instanceof TypeError) !== true;
      var __expect2 = false;
    }}

  