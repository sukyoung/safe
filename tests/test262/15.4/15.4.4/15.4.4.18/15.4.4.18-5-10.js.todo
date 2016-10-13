  function testcase() 
  {
    var result = false;
    var objArray = [];
    function callbackfn(val, idx, obj) 
    {
      result = (this === objArray);
    }
    [11, ].forEach(callbackfn, objArray);
    return result;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  