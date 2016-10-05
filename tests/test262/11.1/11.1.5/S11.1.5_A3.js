  var object = {
    0 : 1,
    "1" : "x",
    o : {
      
    }
  };
  {
    var __result1 = object[0] !== 1;
    var __expect1 = false;
  }
  {
    var __result2 = object["1"] !== "x";
    var __expect2 = false;
  }
  {
    var __result3 = typeof object.o !== "object";
    var __expect3 = false;
  }
  