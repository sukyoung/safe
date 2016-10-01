  function testcase() 
  {
    var obj = {
      prop1 : 1
    };
    var array = Object.keys(obj);
    var desc = Object.getOwnPropertyDescriptor(array, "0");
    return desc.hasOwnProperty("value") && desc.value === "prop1";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  