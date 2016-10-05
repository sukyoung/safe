  function testcase() 
  {
    var arr = [];
    arr[10] = "10";
    arr.length = 20;
    var fromIndex = {
      valueOf : (function () 
      {
        delete arr[10];
        return 3;
      })
    };
    return - 1 === arr.indexOf("10", fromIndex);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  