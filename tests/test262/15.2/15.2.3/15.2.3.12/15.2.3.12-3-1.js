/*
  function testcase() 
  {
    var b = Object.isFrozen(this);
    if (b === false)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
*/
var __result1 = Object.isFrozen(this);
var __expect1 = false;
