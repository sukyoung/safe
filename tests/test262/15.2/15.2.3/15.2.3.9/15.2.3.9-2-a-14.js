  function testcase() 
  {
    var arrObj = [0, 1, 2, ];
    Object.freeze(arrObj);
    var desc = Object.getOwnPropertyDescriptor(arrObj, "0");
    delete arrObj[0];
    return arrObj[0] === 0 && desc.configurable === false && desc.writable === false;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  