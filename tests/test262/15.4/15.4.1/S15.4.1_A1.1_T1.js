  Array.prototype.myproperty = 1;
  var x = Array();
  {
    var __result1 = x.myproperty !== 1;
    var __expect1 = false;
  }
  {
    var __result2 = x.hasOwnProperty('myproperty') !== false;
    var __expect2 = false;
  }
  