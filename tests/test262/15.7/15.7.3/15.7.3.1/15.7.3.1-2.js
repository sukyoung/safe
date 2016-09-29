  function testcase() 
  {
    return Object.getPrototypeOf(new Number(42)) === Number.prototype;
  }
var __result1 = testcase();
var __expect1 = true;
  
