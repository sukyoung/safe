  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return obj instanceof String;
    }
    var obj = new String("abc");
    var newArr = Array.prototype.filter.call(obj, callbackfn);
    return newArr[0] === "a";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  