  var x = new Array("", "", "");
  {
    var __result1 = x.join("") !== "";
    var __expect1 = false;
  }
  var x = new Array("\\", "\\", "\\");
  {
    var __result2 = x.join("\\") !== "\\\\\\\\\\";
    var __expect2 = false;
  }
  var x = new Array("&", "&", "&");
  {
    var __result3 = x.join("&") !== "&&&&&";
    var __expect3 = false;
  }
  var x = new Array(true, true, true);
  {
    var __result4 = x.join() !== "true,true,true";
    var __expect4 = false;
  }
  var x = new Array(null, null, null);
  {
    var __result5 = x.join() !== ",,";
    var __expect5 = false;
  }
  var x = new Array(undefined, undefined, undefined);
  {
    var __result6 = x.join() !== ",,";
    var __expect6 = false;
  }
  var x = new Array(Infinity, Infinity, Infinity);
  {
    var __result7 = x.join() !== "Infinity,Infinity,Infinity";
    var __expect7 = false;
  }
  var x = new Array(NaN, NaN, NaN);
  {
    var __result8 = x.join() !== "NaN,NaN,NaN";
    var __expect8 = false;
  }
  