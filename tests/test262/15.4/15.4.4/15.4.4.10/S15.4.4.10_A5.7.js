  try
{    new Array.prototype.slice();
    $ERROR('#1.1: new Array.prototype.slice() throw TypeError. Actual: ' + (new Array.prototype.slice()));}
  catch (e)
{    {
      var __result1 = (e instanceof TypeError) !== true;
      var __expect1 = false;
    }}

  