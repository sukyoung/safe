  function testcase() 
  {
    var kValue = 'abc';
    function callbackfn(val, idx, obj) 
    {
      return (idx === 5) && (val === kValue);
    }
    var proto = {
      5 : kValue
    };
    var Con = (function () 
    {
      
    });
    Con.prototype = proto;
    var child = new Con();
    child.length = 10;
    var newArr = Array.prototype.filter.call(child, callbackfn);
    return newArr.length === 1 && newArr[0] === kValue;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  