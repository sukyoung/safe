  function testcase() 
  {
    var obj = {
      "123" : 100
    };
    var obj1 = Object.defineProperties(obj, "");
    return obj === obj1;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  