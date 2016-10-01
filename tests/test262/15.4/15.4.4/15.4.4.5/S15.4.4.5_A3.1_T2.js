// [[DefaultValue]]
//  var x = new Array(0, 1, 2, 3);
//  var object = {
//    valueOf : (function () 
//    {
//      return "+";
//    })
//  };
//  {
//    var __result1 = x.join(object) !== "0[object Object]1[object Object]2[object Object]3";
//    var __expect1 = false;
//  }
//  var object = {
//    valueOf : (function () 
//    {
//      return "+";
//    }),
//    toString : (function () 
//    {
//      return "*";
//    })
//  };
//  {
//    var __result2 = x.join(object) !== "0*1*2*3";
//    var __expect2 = false;
//  }
//  var object = {
//    valueOf : (function () 
//    {
//      return "+";
//    }),
//    toString : (function () 
//    {
//      return {
//        
//      };
//    })
//  };
//  {
//    var __result3 = x.join(object) !== "0+1+2+3";
//    var __expect3 = false;
//  }
//  try
//{    var object = {
//      valueOf : (function () 
//      {
//        throw "error";
//      }),
//      toString : (function () 
//      {
//        return "*";
//      })
//    };
//    {
//      var __result4 = x.join(object) !== "0*1*2*3";
//      var __expect4 = false;
//    }}
//  catch (e)
//{    if (e === "error")
//    {
//      $ERROR('#4.2: var object = {valueOf: function() {throw "error"}, toString: function() {return "*"}}; x.join(object) not throw "error"');
//    }
//    else
//    {
//      $ERROR('#4.3: var object = {valueOf: function() {throw "error"}, toString: function() {return "*"}}; x.join(object) not throw Error. Actual: ' + (e));
//    }}
//
//  var object = {
//    toString : (function () 
//    {
//      return "*";
//    })
//  };
//  {
//    var __result5 = x.join(object) !== "0*1*2*3";
//    var __expect5 = false;
//  }
//  var object = {
//    valueOf : (function () 
//    {
//      return {
//        
//      };
//    }),
//    toString : (function () 
//    {
//      return "*";
//    })
//  };
//  {
//    var __result6 = x.join(object) !== "0*1*2*3";
//    var __expect6 = false;
//  }
//  try
//{    var object = {
//      valueOf : (function () 
//      {
//        return "+";
//      }),
//      toString : (function () 
//      {
//        throw "error";
//      })
//    };
//    x.join(object);
//    $ERROR('#7.1: var object = {valueOf: function() {return "+"}, toString: function() {throw "error"}}; x.join(object) throw "error". Actual: ' + (x.join(object)));}
//  catch (e)
//{    {
//      var __result7 = e !== "error";
//      var __expect7 = false;
//    }}
//
//  try
//{    var object = {
//      valueOf : (function () 
//      {
//        return {
//          
//        };
//      }),
//      toString : (function () 
//      {
//        return {
//          
//        };
//      })
//    };
//    x.join(object);
//    $ERROR('#8.1: var object = {valueOf: function() {return {}}, toString: function() {return {}}}; x.join(object) throw TypeError. Actual: ' + (x.join(object)));}
//  catch (e)
//{    {
//      var __result8 = (e instanceof TypeError) !== true;
//      var __expect8 = false;
//    }}
//
//  try
//{    var object = {
//      toString : (function () 
//      {
//        throw "error";
//      })
//    };
//    [].join(object);
//    $ERROR('#9.1: var object = {toString: function() {throw "error"}}; [].join(object) throw "error". Actual: ' + ([].join(object)));}
//  catch (e)
//{    {
//      var __result9 = e !== "error";
//      var __expect9 = false;
//    }}
//
//  
