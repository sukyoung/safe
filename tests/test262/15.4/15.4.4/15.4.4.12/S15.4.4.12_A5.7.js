  try
{    new Array.prototype.splice();
    $ERROR('#1.1: new Array.prototype.splice() throw TypeError. Actual: ' + (new Array.prototype.splice()));}
  catch (e)
{    {
      var __result1 = (e instanceof TypeError) !== true;
      var __expect1 = false;
    }}

  