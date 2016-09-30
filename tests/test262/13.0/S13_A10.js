  function __ziggy__func() 
  {
    return "ziggy stardust";
  }
  var __music_box = {
    
  };
  __music_box.ziggy = __ziggy__func;
  {
    var __result1 = typeof __music_box.ziggy !== "function";
    var __expect1 = false;
  }
  {
    var __result2 = __music_box.ziggy() !== "ziggy stardust";
    var __expect2 = false;
  }
  