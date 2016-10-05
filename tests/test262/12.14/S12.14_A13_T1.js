  function myFunction1() 
  {
    try
{      return 1;}
    catch (err)
{      $ERROR('#1.1: "return 1" inside function does not lead to throwing exception');
      return 0;}

    return 2;
  }
  var x1 = myFunction1();
  {
    var __result1 = x1 !== 1;
    var __expect1 = false;
  }
  function myFunction2() 
  {
    try
{      throw "exc";
      return 1;}
    catch (err)
{      return 2;}

    return 3;
  }
  var x2 = myFunction2();
  {
    var __result2 = x2 !== 2;
    var __expect2 = false;
  }
  function myFunction3() 
  {
    try
{      return someValue;}
    catch (err)
{      return 1;}

    return 2;
  }
  var x3 = myFunction3();
  {
    var __result3 = x3 !== 1;
    var __expect3 = false;
  }
  function myFunction4() 
  {
    try
{      throw "ex1";
      return 1;}
    catch (err)
{      throw "ex2";
      return 0;}

    return 2;
  }
  try
{    var x4 = myFunction4();
    $ERROR('#4.1: Throwing exception inside function lead to throwing exception outside this function');}
  catch (e)
{    {
      var __result4 = e === "ex1";
      var __expect4 = false;
    }
    {
      var __result5 = e !== "ex2";
      var __expect5 = false;
    }}

  