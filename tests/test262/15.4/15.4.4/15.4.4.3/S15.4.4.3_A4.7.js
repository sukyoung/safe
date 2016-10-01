  try
{    new Array.prototype.toLocaleString();
    $ERROR('#1.1: new Array.prototype.toLocaleString() throw TypeError. Actual: ' + (new Array.prototype.toLocaleString()));}
  catch (e)
{    {
      var __result1 = (e instanceof TypeError) !== true;
      var __expect1 = false;
    }}

  