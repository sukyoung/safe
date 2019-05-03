  try
{    new Array.prototype.sort();
    $ERROR('#1.1: new Array.prototype.sort() throw TypeError. Actual: ' + (new Array.prototype.sort()));}
  catch (e)
{    {
      var __result1 = (e instanceof TypeError) !== true;
      var __expect1 = false;
    }}

  