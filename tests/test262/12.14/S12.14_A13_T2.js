  var c1 = 0;
  function myFunction1() 
  {
    try
{      return 1;}
    finally
{      c1 = 1;}

    return 2;
  }
  var x1 = myFunction1();
  {
    var __result1 = x1 !== 1;
    var __expect1 = false;
  }
  {
    var __result2 = c1 !== 1;
    var __expect2 = false;
  }
  var c2 = 0;
  function myFunction2() 
  {
    try
{      throw "exc";
      return 1;}
    finally
{      c2 = 1;}

    return 2;
  }
  try
{    var x2 = myFunction2();
    $ERROR('#2.1: Throwing exception inside function lead to throwing exception outside this function');}
  catch (e)
{    {
      var __result3 = c2 !== 1;
      var __expect3 = false;
    }}

  var c3 = 0;
  function myFunction3() 
  {
    try
{      return someValue;}
    finally
{      c3 = 1;}

    return 2;
  }
  try
{    var x3 = myFunction3();
    $ERROR('#3.1: Throwing exception inside function lead to throwing exception outside this function');}
  catch (e)
{    {
      var __result4 = c3 !== 1;
      var __expect4 = false;
    }}

  var c4 = 0;
  function myFunction4() 
  {
    try
{      return 1;}
    finally
{      c4 = 1;
      throw "exc";
      return 0;}

    return 2;
  }
  try
{    var x4 = myFunction4();
    $ERROR('#4.2: Throwing exception inside function lead to throwing exception outside this function');}
  catch (e)
{    {
      var __result5 = c4 !== 1;
      var __expect5 = false;
    }}

  var c5 = 0;
  function myFunction5() 
  {
    try
{      return 1;}
    finally
{      c5 = 1;
      return someValue;
      return 0;}

    return 2;
  }
  try
{    var x5 = myFunction5();
    $ERROR('#5.2: Throwing exception inside function lead to throwing exception outside this function');}
  catch (e)
{    {
      var __result6 = c5 !== 1;
      var __expect6 = false;
    }}

  var c6 = 0;
  function myFunction6() 
  {
    try
{      throw "ex1";
      return 1;}
    finally
{      c6 = 1;
      throw "ex2";
      return 2;}

    return 3;
  }
  try
{    var x6 = myFunction6();
    $ERROR('#6.1: Throwing exception inside function lead to throwing exception outside this function');}
  catch (e)
{    {
      var __result7 = e === "ex1";
      var __expect7 = false;
    }
    {
      var __result8 = e !== "ex2";
      var __expect8 = false;
    }
    {
      var __result9 = c6 !== 1;
      var __expect9 = false;
    }}

  var c7 = 0;
  function myFunction7() 
  {
    try
{      return 1;}
    finally
{      c7 = 1;
      return 2;}

    return 3;
  }
  var x7 = myFunction7();
  {
    var __result10 = x7 !== 2;
    var __expect10 = false;
  }
  {
    var __result11 = c7 !== 1;
    var __expect11 = false;
  }
  var c8 = 0;
  function myFunction8() 
  {
    try
{      throw "ex1";}
    finally
{      c8 = 1;
      return 2;}

    return 3;
  }
  try
{    var x8 = myFunction8();}
  catch (ex1)
{    c8 = 10;}

  {
    var __result12 = c8 !== 1;
    var __expect12 = false;
  }
  