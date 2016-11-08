  function testcase() 
  {
    var accessed = false;
    function callbackfn(val, idx, obj) 
    {
      accessed = true;
      return val > 10;
    }
    var Foo = (function () 
    {
      
    });
    Foo.prototype = [1, 2, 3, ];
    var obj = new Foo();
    obj.length = 0;
    var testResult = Array.prototype.map.call(obj, callbackfn);
    return testResult.length === 0;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  