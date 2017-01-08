  function testcase() 
  {
    var result = false;
    function callbackfn(val, idx, obj) 
    {
      result = (obj.length === 2);
    }
    var proto = {
      length : 3
    };
    var Con = (function () 
    {
      
    });
    Con.prototype = proto;
    var child = new Con();
    child.length = 2;
    child[0] = 12;
    child[1] = 11;
    child[2] = 9;
    Array.prototype.forEach.call(child, callbackfn);
    return result;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  