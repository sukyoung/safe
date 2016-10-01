// [[DefaultValue]]
//  var obj = {
//    
//  };
//  obj.join = Array.prototype.join;
//  obj.length = {
//    valueOf : (function () 
//    {
//      return 3;
//    })
//  };
//  {
//    var __result1 = obj.join() !== ",,";
//    var __expect1 = false;
//  }
//  obj.length = {
//    valueOf : (function () 
//    {
//      return 3;
//    }),
//    toString : (function () 
//    {
//      return 2;
//    })
//  };
//  {
//    var __result2 = obj.join() !== ",,";
//    var __expect2 = false;
//  }
//  obj.length = {
//    valueOf : (function () 
//    {
//      return 3;
//    }),
//    toString : (function () 
//    {
//      return {
//        
//      };
//    })
//  };
//  {
//    var __result3 = obj.join() !== ",,";
//    var __expect3 = false;
//  }
//  try
//{    obj.length = {
//      valueOf : (function () 
//      {
//        return 3;
//      }),
//      toString : (function () 
//      {
//        throw "error";
//      })
//    };
//    {
//      var __result4 = obj.join() !== ",,";
//      var __expect4 = false;
//    }}
//  catch (e)
//{    if (e === "error")
//    {
//      $ERROR('#4.2: obj.length = {valueOf: function() {return 3}, toString: function() {throw "error"}}; obj.join() not throw "error"');
//    }
//    else
//    {
//      $ERROR('#4.3: obj.length = {valueOf: function() {return 3}, toString: function() {throw "error"}}; obj.join() not throw Error. Actual: ' + (e));
//    }}
//
//  obj.length = {
//    toString : (function () 
//    {
//      return 2;
//    })
//  };
//  {
//    var __result5 = obj.join() !== ",";
//    var __expect5 = false;
//  }
//  obj.length = {
//    valueOf : (function () 
//    {
//      return {
//        
//      };
//    }),
//    toString : (function () 
//    {
//      return 2;
//    })
//  };
//  {
//    var __result6 = obj.join() !== ",";
//    var __expect6 = false;
//  }
//  try
//{    obj.length = {
//      valueOf : (function () 
//      {
//        throw "error";
//      }),
//      toString : (function () 
//      {
//        return 2;
//      })
//    };
//    obj.join();
//    $ERROR('#7.1: obj.length = {valueOf: function() {throw "error"}, toString: function() {return 2}}; obj.join() throw "error". Actual: ' + (obj.join()));}
//  catch (e)
//{    {
//      var __result7 = e !== "error";
//      var __expect7 = false;
//    }}
//
//  try
//{    obj.length = {
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
//    obj.join();
//    $ERROR('#8.1: obj.length = {valueOf: function() {return {}}, toString: function() {return {}}}  obj.join() throw TypeError. Actual: ' + (obj.join()));}
//  catch (e)
//{    {
//      var __result8 = (e instanceof TypeError) !== true;
//      var __expect8 = false;
//    }}
//
//  
