  function testcase() 
  {
    var numObj = new Number(123);
    var preCheck = Object.isExtensible(numObj);
    Object.preventExtensions(numObj);
    numObj[0] = 12;
    return preCheck && ! numObj.hasOwnProperty("0");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  