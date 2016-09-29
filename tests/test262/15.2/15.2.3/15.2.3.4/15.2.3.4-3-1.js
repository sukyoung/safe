  function testcase() 
  {
    var obj = {
      prop1 : 1001
    };
    var arr = Object.getOwnPropertyNames(obj);
    return arr.hasOwnProperty(0) && arr[0] === "prop1";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  