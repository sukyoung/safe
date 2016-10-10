// TODO [[DefineOwnProperty]] for Array object
//  var obj = {
//    
//  };
//  obj.shift = Array.prototype.shift;
//  obj[0] = - 1;
//  obj.length = {
//    valueOf : (function () 
//    {
//      return 1;
//    })
//  };
//  var shift = obj.shift();
//  {
//    var __result1 = shift !== - 1;
//    var __expect1 = false;
//  }
//  obj[0] = - 1;
//  obj.length = {
//    valueOf : (function () 
//    {
//      return 1;
//    }),
//    toString : (function () 
//    {
//      return 0;
//    })
//  };
//  var shift = obj.shift();
//  {
//    var __result2 = shift !== - 1;
//    var __expect2 = false;
//  }
//  obj[0] = - 1;
//  obj.length = {
//    valueOf : (function () 
//    {
//      return 1;
//    }),
//    toString : (function () 
//    {
//      return {
//        
//      };
//    })
//  };
//  var shift = obj.shift();
//  {
//    var __result3 = shift !== - 1;
//    var __expect3 = false;
//  }
//  try
//{    obj[0] = - 1;
//    obj.length = {
//      valueOf : (function () 
//      {
//        return 1;
//      }),
//      toString : (function () 
//      {
//        throw "error";
//      })
//    };
//    var shift = obj.shift();
//    {
//      var __result4 = shift !== - 1;
//      var __expect4 = false;
//    }}
//  catch (e)
//{    if (e === "error")
//    {
//      $ERROR('#4.2: obj[0] = -1; obj.length = {valueOf: function() {return 1}, toString: function() {throw "error"}}; obj.shift() not throw "error"');
//    }
//    else
//    {
//      $ERROR('#4.3: obj[0] = -1; obj.length = {valueOf: function() {return 1}, toString: function() {throw "error"}}; obj.shift() not throw Error. Actual: ' + (e));
//    }}
//
//  obj[0] = - 1;
//  obj.length = {
//    toString : (function () 
//    {
//      return 0;
//    })
//  };
//  var shift = obj.shift();
//  {
//    var __result5 = shift !== undefined;
//    var __expect5 = false;
//  }
//  obj[0] = - 1;
//  obj.length = {
//    valueOf : (function () 
//    {
//      return {
//        
//      };
//    }),
//    toString : (function () 
//    {
//      return 0;
//    })
//  };
//  var shift = obj.shift();
//  {
//    var __result6 = shift !== undefined;
//    var __expect6 = false;
//  }
//  try
//{    obj[0] = - 1;
//    obj.length = {
//      valueOf : (function () 
//      {
//        throw "error";
//      }),
//      toString : (function () 
//      {
//        return 0;
//      })
//    };
//    var shift = obj.shift();
//    $ERROR('#7.1: obj[0] = -1; obj.length = {valueOf: function() {throw "error"}, toString: function() {return 0}}; obj.shift() throw "error". Actual: ' + (shift));}
//  catch (e)
//{    {
//      var __result7 = e !== "error";
//      var __expect7 = false;
//    }}
//
//  try
//{    obj[0] = - 1;
//    obj.length = {
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
//    var shift = obj.shift();
//    $ERROR('#8.1: obj[0] = -1; obj.length = {valueOf: function() {return {}}, toString: function() {return {}}}  obj.shift() throw TypeError. Actual: ' + (shift));}
//  catch (e)
//{    {
//      var __result8 = (e instanceof TypeError) !== true;
//      var __expect8 = false;
//    }}
//
//  
