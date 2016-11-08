  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return obj.length === 2;
    }
    var proto = {
      length : 2
    };
    var Con = (function () 
    {
      
    });
    Con.prototype = proto;
    var child = new Con();
    child[0] = 12;
    child[1] = 11;
    child[2] = 9;
    var newArr = Array.prototype.filter.call(child, callbackfn);
    return newArr.length === 2;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  