  function testcase() 
  {
    var obj = {
      
    };
    var numObj = new Number(- 2);
    numObj.value = "Number";
    Object.defineProperty(obj, "property", numObj);
    return obj.property === "Number";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  