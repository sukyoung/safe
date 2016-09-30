  try
{    new Array.prototype.reverse();
    $ERROR('#1.1: new Array.prototype.reverse() throw TypeError. Actual: ' + (new Array.prototype.reverse()));}
  catch (e)
{    {
      var __result1 = (e instanceof TypeError) !== true;
      var __expect1 = false;
    }}

  