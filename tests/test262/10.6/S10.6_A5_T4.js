  var str = "something different";
  function f1() 
  {
    arguments.length = str;
    return arguments;
  }
  try
{    {
      var __result1 = f1().length !== str;
      var __expect1 = false;
    }}
  catch (e)
{    $ERROR("#1: arguments object don't exists");}

  var f2 = (function () 
  {
    arguments.length = str;
    return arguments;
  });
  try
{    {
      var __result2 = f2().length !== str;
      var __expect2 = false;
    }}
  catch (e)
{    $ERROR("#2: arguments object don't exists");}

  