  function SwitchTest(value) 
  {
    var result = 0;
    switch (value){
      case 0:
        result += 2;

      case 1:
        result += 4;
        break;

      case 2:
        result += 8;

      case 3:
        result += 16;

      default:
        result += 32;
        break;
      case null:
        result += 64;

      case NaN:
        result += 128;
        break;

      case Infinity:
        result += 256;

      case 2 + 3:
        result += 512;
        break;

      case undefined:
        result += 1024;

    }
    return result;
  }
  {
    var __result1 = ! (SwitchTest(0) === 6);
    var __expect1 = false;
  }
  {
    var __result2 = ! (SwitchTest(1) === 4);
    var __expect2 = false;
  }
  {
    var __result3 = ! (SwitchTest(2) === 56);
    var __expect3 = false;
  }
  {
    var __result4 = ! (SwitchTest(3) === 48);
    var __expect4 = false;
  }
  {
    var __result5 = ! (SwitchTest(4) === 32);
    var __expect5 = false;
  }
  {
    var __result6 = ! (SwitchTest(5) === 512);
    var __expect6 = false;
  }
  {
    var __result7 = ! (SwitchTest(true) === 32);
    var __expect7 = false;
  }
  {
    var __result8 = ! (SwitchTest(false) === 32);
    var __expect8 = false;
  }
  {
    var __result9 = ! (SwitchTest(null) === 192);
    var __expect9 = false;
  }
  {
    var __result10 = ! (SwitchTest(void 0) === 1024);
    var __expect10 = false;
  }
  {
    var __result11 = ! (SwitchTest(NaN) === 32);
    var __expect11 = false;
  }
  {
    var __result12 = ! (SwitchTest(Infinity) === 768);
    var __expect12 = false;
  }
  