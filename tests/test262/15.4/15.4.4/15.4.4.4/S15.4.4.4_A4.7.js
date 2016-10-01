  try
{    new Array.prototype.concat();
    $ERROR('#1.1: new Array.prototype.concat() throw TypeError. Actual: ' + (new Array.prototype.concat()));}
  catch (e)
{    {
      var __result1 = (e instanceof TypeError) !== true;
      var __expect1 = false;
    }}

  