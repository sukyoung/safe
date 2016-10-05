  var object = {
    p1 : 1,
    p2 : 1
  };
  var result = 0;
  lbl : for(var i in object)
  {
    result += object[i];
    break lbl;
  }
  {
    var __result1 = ! (result === 1);
    var __expect1 = false;
  }
  