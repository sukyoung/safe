  function testcase() 
  {
    var called = 0;
    var result = false;
    function callbackfn(val, idx, obj) 
    {
      called++;
      if (val === 11)
      {
        result = true;
      }
      return true;
    }
    var obj = {
      0 : 9,
      non_index_property : 11,
      length : 20
    };
    var testResult = Array.prototype.map.call(obj, callbackfn);
    return ! result && testResult[0] === true && called === 1;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  