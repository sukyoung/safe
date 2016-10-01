  var __constr = String.prototype.constructor;
  var __instance = new __constr("choosing one");
  {
    var __result1 = __instance != "choosing one";
    var __expect1 = false;
  }
  {
    var __result2 = __instance.constructor !== String;
    var __expect2 = false;
  }
  {
    var __result3 = ! (String.prototype.isPrototypeOf(__instance));
    var __expect3 = false;
  }
  var __to_string_result = '[object ' + 'String' + ']';
  delete String.prototype.toString;
  {
    var __result4 = __instance.toString() !== __to_string_result;
    var __expect4 = false;
  }
  