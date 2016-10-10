  try
{    null();
    $ERROR('#1.1: null() throw TypeError. Actual: ' + (null()));}
  catch (e)
{    {
      var __result1 = (e instanceof TypeError) !== true;
      var __expect1 = false;
    }}

  try
{    var x = null;
    x();
    $ERROR('#2.1: var x = null; x() throw TypeError. Actual: ' + (x()));}
  catch (e)
{    {
      var __result2 = (e instanceof TypeError) !== true;
      var __expect2 = false;
    }}

  