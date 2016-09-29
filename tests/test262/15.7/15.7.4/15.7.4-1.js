  function testcase() 
  {
    var numProto = Object.getPrototypeOf(new Number(42));
    var s = Object.prototype.toString.call(numProto);
    return (s === '[object Number]');
  }

      var __result1 = testcase();
      var __expect1 = true;
  
