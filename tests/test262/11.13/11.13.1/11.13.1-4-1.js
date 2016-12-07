var __globalObject = @Global;
function fnGlobalObject() {
     return __globalObject;
}
  function testcase() 
  {
    function foo() 
    {
      __ES3_1_test_suite_test_11_13_1_unique_id_3__ = 42;
    }
    foo();
    var desc = Object.getOwnPropertyDescriptor(fnGlobalObject(), '__ES3_1_test_suite_test_11_13_1_unique_id_3__');
    if (desc.value === 42 && desc.writable === true && desc.enumerable === true && desc.configurable === true)
    {
      delete __ES3_1_test_suite_test_11_13_1_unique_id_3__;
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
