  var constr = Object.prototype.constructor;
  var obj = new constr;
  {
    var __result1 = obj === undefined;
    var __expect1 = false;
  }
  {
    var __result2 = obj.constructor !== Object;
    var __expect2 = false;
  }
  {
    var __result3 = ! (Object.prototype.isPrototypeOf(obj));
    var __expect3 = false;
  }
  var to_string_result = '[object ' + 'Object' + ']';
  {
    var __result4 = obj.toString() !== to_string_result;
    var __expect4 = false;
  }
  {
    var __result5 = obj.valueOf().toString() !== to_string_result;
    var __expect5 = false;
  }
  