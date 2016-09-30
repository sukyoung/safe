  try
{    new Array.prototype.shift();
    $ERROR('#1.1: new Array.prototype.shift() throw TypeError. Actual: ' + (new Array.prototype.shift()));}
  catch (e)
{    {
      var __result1 = (e instanceof TypeError) !== true;
      var __expect1 = false;
    }}

  