  function testcase() 
  {
    var result = false;
    function callbackfn(val, idx, o) 
    {
      result = (5 === this.valueOf());
    }
    var obj = {
      0 : 11,
      length : 2
    };
    Array.prototype.forEach.call(obj, callbackfn, 5);
    return result;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  