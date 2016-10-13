// [[DefaultValue]]
//  var object = {
//    valueOf : (function () 
//    {
//      return "+";
//    })
//  };
//  var x = new Array(object);
//  {
//    var __result1 = x.join() !== "[object Object]";
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
//  var x = new Array(object);
//  {
//    var __result2 = x.join() !== "*";
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
//  var x = new Array(object);
//  {
//    var __result3 = x.join() !== "+";
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
//    var x = new Array(object);
//    {
//      var __result4 = x.join() !== "*";
//      var __expect4 = false;
//    }}
//  catch (e)
//{    if (e === "error")
//    {
//      $ERROR('#4.2: var object = {valueOf: function() {throw "error"}, toString: function() {return "*"}} var x = new Array(object); x.join() not throw "error"');
//    }
//    else
//    {
//      $ERROR('#4.3: var object = {valueOf: function() {throw "error"}, toString: function() {return "*"}} var x = new Array(object); x.join() not throw Error. Actual: ' + (e));
//    }}
//
//  var object = {
//    toString : (function () 
//    {
//      return "*";
//    })
//  };
//  var x = new Array(object);
//  {
//    var __result5 = x.join() !== "*";
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
//  var x = new Array(object);
//  {
//    var __result6 = x.join() !== "*";
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
//    var x = new Array(object);
//    x.join();
//    $ERROR('#7.1: var object = {valueOf: function() {return "+"}, toString: function() {throw "error"}} var x = new Array(object); x.join() throw "error". Actual: ' + (x.join()));}
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
//    var x = new Array(object);
//    x.join();
//    $ERROR('#8.1: var object = {valueOf: function() {return {}}, toString: function() {return {}}} var x = new Array(object); x.join() throw TypeError. Actual: ' + (x.join()));}
//  catch (e)
//{    {
//      var __result8 = (e instanceof TypeError) !== true;
//      var __expect8 = false;
//    }}
//
//  
