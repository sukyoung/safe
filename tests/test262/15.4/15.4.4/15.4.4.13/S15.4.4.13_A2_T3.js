// XXX
//  var obj = {
//    
//  };
//  obj.unshift = Array.prototype.unshift;
//  obj.length = {
//    valueOf : (function () 
//    {
//      return 3;
//    })
//  };
//  var unshift = obj.unshift();
//  {
//    var __result1 = unshift !== 3;
//    var __expect1 = false;
//  }
//  obj.length = {
//    valueOf : (function () 
//    {
//      return 3;
//    }),
//    toString : (function () 
//    {
//      return 1;
//    })
//  };
//  var unshift = obj.unshift();
//  {
//    var __result2 = unshift !== 3;
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
//  var unshift = obj.unshift();
//  {
//    var __result3 = unshift !== 3;
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
//    var unshift = obj.unshift();
//    {
//      var __result4 = unshift !== 3;
//      var __expect4 = false;
//    }}
//  catch (e)
//{    if (e === "error")
//    {
//      $ERROR('#4.2:  obj.length = {valueOf: function() {return 3}, toString: function() {throw "error"}}; obj.unshift() not throw "error"');
//    }
//    else
//    {
//      $ERROR('#4.3:  obj.length = {valueOf: function() {return 3}, toString: function() {throw "error"}}; obj.unshift() not throw Error. Actual: ' + (e));
//    }}
//
//  obj.length = {
//    toString : (function () 
//    {
//      return 1;
//    })
//  };
//  var unshift = obj.unshift();
//  {
//    var __result5 = unshift !== 1;
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
//      return 1;
//    })
//  };
//  var unshift = obj.unshift();
//  {
//    var __result6 = unshift !== 1;
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
//        return 1;
//      })
//    };
//    var unshift = obj.unshift();
//    $ERROR('#7.1:  obj.length = {valueOf: function() {throw "error"}, toString: function() {return 1}}; obj.unshift() throw "error". Actual: ' + (unshift));}
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
//    var unshift = obj.unshift();
//    $ERROR('#8.1:  obj.length = {valueOf: function() {return {}}, toString: function() {return {}}}  obj.unshift() throw TypeError. Actual: ' + (unshift));}
//  catch (e)
//{    {
//      var __result8 = (e instanceof TypeError) !== true;
//      var __expect8 = false;
//    }}
//
//  
