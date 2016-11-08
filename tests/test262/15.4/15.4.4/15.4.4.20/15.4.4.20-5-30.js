  function testcase() 
  {
    function innerObj() 
    {
      this._15_4_4_20_5_30 = true;
      var _15_4_4_20_5_30 = false;
      function callbackfn(val, idx, obj) 
      {
        return this._15_4_4_20_5_30;
      }
      var srcArr = [1, ];
      var resArr = srcArr.filter(callbackfn);
      this.retVal = resArr.length === 0;
    }
    return new innerObj().retVal;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  