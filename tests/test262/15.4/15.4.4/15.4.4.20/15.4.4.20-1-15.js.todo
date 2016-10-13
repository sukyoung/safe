  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return '[object Arguments]' === Object.prototype.toString.call(obj);
    }
    var obj = (function () 
    {
      return arguments;
    })("a", "b");
    var newArr = Array.prototype.filter.call(obj, callbackfn);
    return newArr[0] === "a" && newArr[1] === "b";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  