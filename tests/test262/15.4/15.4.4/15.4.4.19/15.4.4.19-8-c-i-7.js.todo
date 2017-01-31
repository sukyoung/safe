  function testcase() 
  {
    var kValue = "abc";
    function callbackfn(val, idx, obj) 
    {
      if (idx === 5)
      {
        return val === kValue;
      }
      return false;
    }
    var proto = {
      5 : kValue,
      length : 10
    };
    var Con = (function () 
    {
      
    });
    Con.prototype = proto;
    var child = new Con();
    var newArr = Array.prototype.map.call(child, callbackfn);
    return newArr[5] === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  