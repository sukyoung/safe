  function testcase() 
  {
  "use strict";
    var numBak = Number;
    try
{      Number = 12;
      return true;}
    finally
{      Number = numBak;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  