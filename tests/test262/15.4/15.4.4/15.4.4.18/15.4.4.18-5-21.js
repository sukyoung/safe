var __globalObject = @Global;
function fnGlobalObject() {
	return __globalObject;
}

  function testcase() 
  {
    var result = false;
    function callbackfn(val, idx, obj) 
    {
      result = (this === fnGlobalObject());
    }
    [11, ].forEach(callbackfn, fnGlobalObject());
    return result;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
