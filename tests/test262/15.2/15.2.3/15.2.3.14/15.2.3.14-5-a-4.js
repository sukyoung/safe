  function testcase() 
  {
    var obj = {
      prop1 : 100
    };
    var array = Object.keys(obj);
    var desc = Object.getOwnPropertyDescriptor(array, "0");
    delete array[0];
    return typeof array[0] === "undefined" && desc.hasOwnProperty("configurable") && desc.configurable === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  