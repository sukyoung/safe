  function testcase() 
  {
    var accessed = false;
    function callbackfn(val, idx, obj) 
    {
      accessed = true;
      return true;
    }
    var obj = {
      0 : 9,
      length : "asdf!_"
    };
    var newArr = Array.prototype.filter.call(obj, callbackfn);
    return ! accessed && newArr.length === 0;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  