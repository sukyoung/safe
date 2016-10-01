  try
{    new Array.prototype.toString();
    $ERROR('#1.1: new Array.prototype.toString() throw TypeError. Actual: ' + (new Array.prototype.toString()));}
  catch (e)
{    {
      var __result1 = (e instanceof TypeError) !== true;
      var __expect1 = false;
    }}

  