  try
{    undefined();
    $ERROR('#1.1: undefined() throw TypeError. Actual: ' + (e));}
  catch (e)
{    {
      var __result1 = (e instanceof TypeError) !== true;
      var __expect1 = false;
    }}

  try
{    var x = undefined;
    x();
    $ERROR('#2.1: var x = undefined; x() throw TypeError. Actual: ' + (e));}
  catch (e)
{    {
      var __result2 = (e instanceof TypeError) !== true;
      var __expect2 = false;
    }}

  