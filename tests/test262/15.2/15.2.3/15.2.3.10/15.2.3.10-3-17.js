  function testcase() 
  {
    var numObj = new Number(123);
    var preCheck = Object.isExtensible(numObj);
    Object.preventExtensions(numObj);
    numObj.exName = 2;
    return preCheck && ! numObj.hasOwnProperty("exName");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  