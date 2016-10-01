  function testcase() 
  {
    var obj = {
      
    };
    var obj1 = Object.defineProperties(obj, false);
    return obj === obj1;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  