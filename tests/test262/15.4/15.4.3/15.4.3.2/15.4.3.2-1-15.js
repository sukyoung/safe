var __globalObject = @Global;
function fnGlobalObject() {
	return __globalObject;
}

  function testcase() 
  {
    return ! Array.isArray(fnGlobalObject());
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
