// TODO Array.prototype.filter
  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return obj.length === 3;
    }
    var str = new String("012");
    var newArr = Array.prototype.filter.call(str, callbackfn);
    return newArr.length === 3;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
