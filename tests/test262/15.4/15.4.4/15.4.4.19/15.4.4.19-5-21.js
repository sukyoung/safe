var __globalObject = @Global;
function fnGlobalObject() {
	return __globalObject;
}
  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return this === fnGlobalObject();
    }
    var testResult = [11, ].map(callbackfn, fnGlobalObject());
    return testResult[0] === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
