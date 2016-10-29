  var ARG_STRING = "value of the argument property";
  function f1() 
  {
    this.constructor.prototype.arguments = ARG_STRING;
    return arguments;
  }
var __result1 = (new f1(1, 2, 3, 4, 5)).length !== 5;
var __expect1 = false;
var __result2 = (new f1(1, 2, 3, 4, 5))[3] !== 4;
var __expect2 = false;
  var x = new f1(1, 2, 3, 4, 5);
var __result3 = delete x[3] !== true;
var __expect3 = false;
var __result4 = x[3] === 4;
var __expect4 = false;
