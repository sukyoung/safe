  try
{    null.toString();
    $ERROR('#1.1: null.toString() throw TypeError. Actual: ' + (null.toString()));}
  catch (e)
{    {
      var __result1 = (e instanceof TypeError) !== true;
      var __expect1 = false;
    }}

  try
{    null["toString"]();
    $ERROR('#2.1: null["toString"]() throw TypeError. Actual: ' + (null["toString"]()));}
  catch (e)
{    {
      var __result2 = (e instanceof TypeError) !== true;
      var __expect2 = false;
    }}

  