  function testcase() 
  {
    var obj = {
      prop1 : 1001,
      prop2 : (function () 
      {
        return 1002;
      })
    };
    Object.defineProperty(obj, "prop3", {
      value : 1003,
      enumerable : false,
      configurable : true
    });
    Object.defineProperty(obj, "prop4", {
      get : (function () 
      {
        return 1004;
      }),
      enumerable : false,
      configurable : true
    });
    var arr = Object.keys(obj);
    return (arr.length === 2) && (arr[0] === "prop1") && (arr[1] === "prop2");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  