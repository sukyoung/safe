  function testcase() 
  {
    var obj = {
      "a" : "a"
    };
    var result = Object.getOwnPropertyNames(obj);
    var beforeDeleted = (result.hasOwnProperty("0"));
    delete result[0];
    var afterDeleted = (result.hasOwnProperty("0"));
    return beforeDeleted && ! afterDeleted;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  