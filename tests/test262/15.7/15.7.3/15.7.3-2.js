  function testcase() 
  {
    var p = Object.getPrototypeOf(Number);
    if (p === Function.prototype)
    {
      return true;
    }
  }
var __result1 = testcase();
var __expect1 = true;
