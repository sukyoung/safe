  function testcase() 
  {
  "use strict";
    var objBak = Object;
    try
{      Object = 12;
      return true;}
    finally
{      Object = objBak;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  