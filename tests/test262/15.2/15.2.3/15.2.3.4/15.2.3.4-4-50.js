  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperty(obj, "nonEnumerableProp", {
      value : 10,
      enumerable : false,
      configurable : true
    });
    var result = Object.getOwnPropertyNames(obj);
    return result[0] === "nonEnumerableProp";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  