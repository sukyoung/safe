  var c1 = 0;
  function myFunction1() 
  {
    try
{      return 1;}
    catch (err)
{      $ERROR('#1.1: "return 1" inside function does not lead to throwing exception');
      return 0;}

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
    catch (err)
{      return 0;}

    finally
{      c2 = 1;}

    return 2;
  }
  var x2 = myFunction2();
  {
    var __result3 = c2 !== 1;
    var __expect3 = false;
  }
  {
    var __result4 = x2 !== 0;
    var __expect4 = false;
  }
  var c3 = 0;
  function myFunction3() 
  {
    try
{      return someValue;}
    catch (err)
{      return 1;}

    finally
{      c3 = 1;}

    return 2;
  }
  var x3 = myFunction3();
  {
    var __result5 = c3 !== 1;
    var __expect5 = false;
  }
  {
    var __result6 = x3 !== 1;
    var __expect6 = false;
  }
  var c4 = 0;
  function myFunction4() 
  {
    try
{      throw "ex1";
      return 1;}
    catch (err)
{      throw "ex2";
      return 0;}

    finally
{      c4 = 1;}

    return 2;
  }
  try
{    var x4 = myFunction4();
    $ERROR('#4.1: Throwing exception inside function lead to throwing exception outside this function');}
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
      var __result9 = c4 !== 1;
      var __expect9 = false;
    }}

  var c5 = 0;
  function myFunction5() 
  {
    try
{      throw "ex1";
      return 1;}
    catch (err)
{      return 0;}

    finally
{      c5 = 1;
      throw "ex2";}

    return 2;
  }
  try
{    var x5 = myFunction5();
    $ERROR('#5.1: Throwing exception inside function lead to throwing exception outside this function');}
  catch (e)
{    {
      var __result10 = e === "ex1";
      var __expect10 = false;
    }
    {
      var __result11 = e !== "ex2";
      var __expect11 = false;
    }
    {
      var __result12 = c5 !== 1;
      var __expect12 = false;
    }}

  var c6 = 0;
  function myFunction6() 
  {
    try
{      throw "ex1";
      return 1;}
    catch (err)
{      throw "ex2";
      return 0;}

    finally
{      c6 = 1;
      throw "ex3";}

    return 2;
  }
  try
{    var x6 = myFunction6();
    $ERROR('#6.1: Throwing exception inside function lead to throwing exception outside this function');}
  catch (e)
{    {
      var __result13 = e === "ex1";
      var __expect13 = false;
    }
    {
      var __result14 = e === "ex2";
      var __expect14 = false;
    }
    {
      var __result15 = e !== "ex3";
      var __expect15 = false;
    }
    if (c6 !== 1)
      $ERROR('#6.5: "finally" block must be evaluated');}

  var c7 = 0;
  function myFunction7() 
  {
    try
{      throw "ex1";
      return 1;}
    catch (err)
{      throw "ex2";
      return 0;}

    finally
{      c7 = 1;
      return 2;}

    return 3;
  }
  try
{    var x7 = myFunction7();
    if (x7 !== 2)
      $ERROR('#7.1: x7===2. Actual: x7===' + x7);}
  catch (e)
{    }

  if (c7 !== 1)
    $ERROR('#7.2: "finally" block must be evaluated');
  