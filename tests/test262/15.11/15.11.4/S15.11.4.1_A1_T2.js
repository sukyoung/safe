  constr = Error.prototype.constructor;
  err = new constr;
  {
    var __result1 = err === undefined;
    var __expect1 = false;
  }
  {
    var __result2 = err.constructor !== Error;
    var __expect2 = false;
  }
  {
    var __result3 = ! (Error.prototype.isPrototypeOf(err));
    var __expect3 = false;
  }
  Error.prototype.toString = Object.prototype.toString;
  to_string_result = '[object ' + 'Error' + ']';
  {
    var __result4 = err.toString() !== to_string_result;
    var __expect4 = false;
  }
  {
    var __result5 = err.valueOf().toString() !== to_string_result;
    var __expect5 = false;
  }
  