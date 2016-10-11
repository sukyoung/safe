  function Palette() 
  {
    
  }
  ;
  Palette.prototype = {
    red : 0xff0000,
    green : 0xff00
  };
  var __palette = new Palette;
  {
    var __result1 = __palette.red !== 0xff0000;
    var __expect1 = false;
  }
  {
    var __result2 = delete __palette.red !== true;
    var __expect2 = false;
  }
  {
    var __result3 = __palette.red !== 0xff0000;
    var __expect3 = false;
  }
  