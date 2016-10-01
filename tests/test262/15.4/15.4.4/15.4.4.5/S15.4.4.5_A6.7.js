  try
{    new Array.prototype.join();
    $ERROR('#1.1: new Array.prototype.join() throw TypeError. Actual: ' + (new Array.prototype.join()));}
  catch (e)
{    {
      var __result1 = (e instanceof TypeError) !== true;
      var __expect1 = false;
    }}

  