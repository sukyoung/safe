  try
{    new Boolean(true)();
    $ERROR('#1.1: new Boolean(true)() throw TypeError. Actual: ' + (new Boolean(true)()));}
  catch (e)
{    {
      var __result1 = (e instanceof TypeError) !== true;
      var __expect1 = false;
    }}

  try
{    var x = new Boolean(true);
    x();
    $ERROR('#2.1: var x = new Boolean(true); x() throw TypeError. Actual: ' + (x()));}
  catch (e)
{    {
      var __result2 = (e instanceof TypeError) !== true;
      var __expect2 = false;
    }}

  