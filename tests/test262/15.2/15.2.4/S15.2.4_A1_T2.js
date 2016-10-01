  {
    var __result1 = Object.prototype.toString() == false;
    var __expect1 = false;
  }
  delete Object.prototype.toString;
  try
{    Object.prototype.toString();
    $ERROR('#2: Object prototype object has not prototype');}
  catch (e)
{    {
      var __result2 = (e instanceof TypeError) !== true;
      var __expect2 = false;
    }}

  