  var obj = new Object();
  var objRef = obj;
  objRef.oneProperty = - 1;
  obj.oneProperty = true;
  {
    var __result1 = objRef.oneProperty !== true;
    var __expect1 = false;
  }
  ;
  