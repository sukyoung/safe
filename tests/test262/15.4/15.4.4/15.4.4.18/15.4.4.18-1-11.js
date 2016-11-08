  function testcase() 
  {
    var result = false;
    function callbackfn(val, idx, obj) 
    {
      result = obj instanceof Date;
    }
    var obj = new Date();
    obj.length = 1;
    obj[0] = 1;
    Array.prototype.forEach.call(obj, callbackfn);
    return result;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  