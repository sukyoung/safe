  function MyObject(val) 
  {
    this.value = val;
    this.valueOf = (function () 
    {
      return this.value;
    });
  }
  var x = new MyObject(1);
  var y = Object(x);
  {
    var __result1 = y.valueOf() !== x.valueOf();
    var __expect1 = false;
  }
  {
    var __result2 = typeof y !== typeof x;
    var __expect2 = false;
  }
  {
    var __result3 = y.constructor.prototype !== x.constructor.prototype;
    var __expect3 = false;
  }
  {
    var __result4 = y !== x;
    var __expect4 = false;
  }
  