  function f1() 
  {
    for(var x in arguments)
    {
      if (x === "length")
      {
        return false;
      }
    }
    return true;
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
    for(var x in arguments)
    {
      if (x === "length")
      {
        return false;
      }
    }
    return true;
  });
  try
{    {
      var __result2 = ! f2();
      var __expect2 = false;
    }}
  catch (e)
{    $ERROR("#2: arguments object don't exists");}

  