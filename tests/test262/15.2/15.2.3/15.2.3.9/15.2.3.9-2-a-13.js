  function testcase() 
  {
    var obj = {
      0 : 0,
      1 : 1,
      length : 2
    };
    Object.freeze(obj);
    var desc = Object.getOwnPropertyDescriptor(obj, "0");
    delete obj[0];
    return obj[0] === 0 && desc.configurable === false && desc.writable === false;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  