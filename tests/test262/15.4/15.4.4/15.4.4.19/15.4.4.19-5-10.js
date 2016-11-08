  function testcase() 
  {
    var objArray = new Array(2);
    function callbackfn(val, idx, obj) 
    {
      return this === objArray;
    }
    var testResult = [11, ].map(callbackfn, objArray);
    return testResult[0] === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  