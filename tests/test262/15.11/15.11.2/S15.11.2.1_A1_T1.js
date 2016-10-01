  function otherScope(msg) 
  {
    return new Error(msg);
  }
  var err1 = new Error('msg1');
  {
    var __result1 = err1.message !== "msg1";
    var __expect1 = false;
  }
  var err2 = otherScope('msg2');
  {
    var __result2 = err2.message !== "msg2";
    var __expect2 = false;
  }
  var err3 = otherScope();
  {
    var __result3 = err3.hasOwnProperty('message');
    var __expect3 = false;
  }
/*
  var err4 = eval("new Error('msg4')");
  {
    var __result4 = err4.message !== "msg4";
    var __expect4 = false;
  }
*/
