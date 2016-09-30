  try
{    new Array.prototype.push();
    $ERROR('#1.1: new Array.prototype.push() throw TypeError. Actual: ' + (new Array.prototype.push()));}
  catch (e)
{    {
      var __result1 = (e instanceof TypeError) !== true;
      var __expect1 = false;
    }}

  