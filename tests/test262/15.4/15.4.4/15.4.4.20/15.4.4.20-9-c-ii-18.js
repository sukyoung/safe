  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return 'hello' === this.valueOf();
    }
    var obj = {
      0 : 11,
      length : 2
    };
    var newArr = Array.prototype.filter.call(obj, callbackfn, "hello");
    return newArr.length === 1 && newArr[0] === 11;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  