  function testcase() 
  {
    var obj = {
      
    };
    Object.defineProperties(obj, {
      prop : {
        configurable : true,
        enumerable : true
      }
    });
    var desc = Object.getOwnPropertyDescriptor(obj, "prop");
    return desc.hasOwnProperty("value") && typeof desc.value === "undefined" && desc.hasOwnProperty("writable") && desc.writable === false && desc.hasOwnProperty("configurable") && desc.configurable === true && desc.hasOwnProperty("enumerable") && desc.enumerable === true && ! desc.hasOwnProperty("get") && ! desc.hasOwnProperty("set");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  