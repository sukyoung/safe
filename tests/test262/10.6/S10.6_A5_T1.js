  function f1() 
  {
    return arguments.hasOwnProperty("length");
  }
  try
{    {
      var __result1 = f1() !== true;
      var __expect1 = false;
    }}
  catch (e)
{    $ERROR("#1: arguments object doesn't exists");}

  var f2 = (function () 
  {
    return arguments.hasOwnProperty("length");
  });
  try
{    {
      var __result2 = f2() !== true;
      var __expect2 = false;
    }}
  catch (e)
{    $ERROR("#2: arguments object doesn't exists");}

  