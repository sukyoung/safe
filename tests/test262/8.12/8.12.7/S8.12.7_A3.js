  var BLUE_NUM = 1;
  var BLUE_STR = "1";
  var YELLOW_NUM = 2;
  var YELLOW_STR = "2";
  var __color__map = {
    red : 0xff0000,
    BLUE_NUM : 0xff,
    green : 0xff00,
    YELLOW_STR : 0xffff00
  };
  {
    var __result1 = delete __color__map[YELLOW_NUM] !== true;
    var __expect1 = false;
  }
  ;
  {
    var __result2 = __color__map[YELLOW_STR] !== undefined;
    var __expect2 = false;
  }
  {
    var __result3 = delete __color__map[BLUE_STR] !== true;
    var __expect3 = false;
  }
  ;
  {
    var __result4 = __color__map[BLUE_NUM] !== undefined;
    var __expect4 = false;
  }
  