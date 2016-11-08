  function testcase() 
  {
    var result = false;
    var objString = new String();
    function callbackfn(val, idx, obj) 
    {
      result = (this === objString);
    }
    [11, ].forEach(callbackfn, objString);
    return result;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  