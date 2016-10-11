  function f1() 
  {
    return (delete arguments.callee);
  }
  try
{    {
      var __result1 = ! f1();
      var __expect1 = false;
    }}
  catch (e)
{    $ERROR("#1: arguments object don't exists");}

  var f2 = (function () 
  {
    return (delete arguments.callee);
  });
  try
{    {
      var __result2 = ! f2();
      var __expect2 = false;
    }}
  catch (e)
{    $ERROR("#2: arguments object don't exists");}

  