  function f1() 
  {
    return arguments.callee;
  }
  try
{    {
      var __result1 = f1 !== f1();
      var __expect1 = false;
    }}
  catch (e)
{    $ERROR("#1: arguments object doesn't exists");}

  var f2 = (function () 
  {
    return arguments.callee;
  });
  try
{    {
      var __result2 = f2 !== f2();
      var __expect2 = false;
    }}
  catch (e)
{    $ERROR("#1: arguments object doesn't exists");}

  