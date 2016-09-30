  function testcase() 
  {
    var obj = {
      prop1 : 1001,
      prop2 : 1002
    };
    Object.defineProperty(obj, "prop3", {
      value : 1003,
      enumerable : true,
      configurable : true
    });
    Object.defineProperty(obj, "prop4", {
      get : (function () 
      {
        return 1003;
      }),
      enumerable : true,
      configurable : true
    });
    var arr = Object.keys(obj);
    return arr.hasOwnProperty(0) && arr[0] === "prop1";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  