  try
{    new Array.prototype.pop();
    $ERROR('#1.1: new Array.prototype.pop() throw TypeError. Actual: ' + (new Array.prototype.pop()));}
  catch (e)
{    {
      var __result1 = (e instanceof TypeError) !== true;
      var __expect1 = false;
    }}

  