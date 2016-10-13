  function testcase() 
  {
    var result = false;
    function callbackfn(val, idx, obj) 
    {
      result = obj instanceof Boolean;
    }
    var obj = new Boolean(true);
    obj.length = 2;
    obj[0] = 11;
    obj[1] = 12;
    Array.prototype.forEach.call(obj, callbackfn);
    return result;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  