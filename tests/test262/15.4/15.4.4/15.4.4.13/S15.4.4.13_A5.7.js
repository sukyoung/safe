  try
{    new Array.prototype.unshift();
    $ERROR('#1.1: new Array.prototype.unshift() throw TypeError. Actual: ' + (new Array.prototype.unshift()));}
  catch (e)
{    {
      var __result1 = (e instanceof TypeError) !== true;
      var __expect1 = false;
    }}

  