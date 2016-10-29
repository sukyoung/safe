  function f1() 
  {
    return arguments.constructor.prototype;
  }
  try
{    {
      var __result1 = f1() !== Object.prototype;
      var __expect1 = false;
    }}
  catch (e)
{    $ERROR("#1: arguments doesn't exists");}

  var f2 = (function () 
  {
    return arguments.constructor.prototype;
  });
  try
{    {
      var __result2 = f2() !== Object.prototype;
      var __expect2 = false;
    }}
  catch (e)
{    $ERROR("#2: arguments doesn't exists");}

  