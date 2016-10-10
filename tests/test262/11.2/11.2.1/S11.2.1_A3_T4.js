  try
{    undefined.toString();
    $ERROR('#1.1: undefined.toString() throw TypeError. Actual: ' + (undefined.toString()));}
  catch (e)
{    {
      var __result1 = (e instanceof TypeError) !== true;
      var __expect1 = false;
    }}

  try
{    undefined["toString"]();
    $ERROR('#2.1: undefined["toString"]() throw TypeError. Actual: ' + (undefined["toString"]()));}
  catch (e)
{    {
      var __result2 = (e instanceof TypeError) !== true;
      var __expect2 = false;
    }}

  