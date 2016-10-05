  function testcase() 
  {
    var arr = [1, 2, , 1, 2, ];
    var elementThirdAccessed = false;
    var elementFifthAccessed = false;
    Object.defineProperty(arr, "2", {
      get : (function () 
      {
        elementThirdAccessed = true;
        return 2;
      }),
      configurable : true
    });
    Object.defineProperty(arr, "4", {
      get : (function () 
      {
        elementFifthAccessed = true;
        return 2;
      }),
      configurable : true
    });
    arr.indexOf(2);
    return ! elementThirdAccessed && ! elementFifthAccessed;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  