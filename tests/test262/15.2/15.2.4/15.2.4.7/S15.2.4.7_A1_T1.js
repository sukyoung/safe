  {
    var __result1 = typeof Object.prototype.propertyIsEnumerable !== "function";
    var __expect1 = false;
  }
  var proto = {
    rootprop : "avis"
  };
  function AVISFACTORY(name) 
  {
    this.name = name;
  }
  ;
  AVISFACTORY.prototype = proto;
  var seagull = new AVISFACTORY("seagull");
  {
    var __result2 = typeof seagull.propertyIsEnumerable !== "function";
    var __expect2 = false;
  }
  {
    var __result3 = ! (seagull.propertyIsEnumerable("name"));
    var __expect3 = false;
  }
  {
    var __result4 = seagull.propertyIsEnumerable("rootprop");
    var __expect4 = false;
  }
  