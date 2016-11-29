  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return (idx === 5) && (val === "abc");
    }
    var proto = {
      0 : 11,
      5 : 100
    };
    var Con = (function () 
    {
      
    });
    Con.prototype = proto;
    var child = new Con();
    child[5] = "abc";
    child.length = 10;
    var newArr = Array.prototype.filter.call(child, callbackfn);
    return newArr.length === 1 && newArr[0] === "abc";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  