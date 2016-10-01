//  TODO [[DefaultValue]]
//  var obj = {
//    
//  };
//  obj.pop = Array.prototype.pop;
//  obj[0] = - 1;
//  obj.length = {
//    valueOf : (function () 
//    {
//      return 1;
//    })
//  };
//  var pop = obj.pop();
//  {
//    var __result1 = pop !== - 1;
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
//  var pop = obj.pop();
//  {
//    var __result2 = pop !== - 1;
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
//  var pop = obj.pop();
//  {
//    var __result3 = pop !== - 1;
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
//    var pop = obj.pop();
//    {
//      var __result4 = pop !== - 1;
//      var __expect4 = false;
//    }}
//  catch (e)
//{    if (e === "error")
//    {
//      $ERROR('#4.2: obj[0] = -1; obj.length = {valueOf: function() {return 1}, toString: function() {throw "error"}}; obj.pop() not throw "error"');
//    }
//    else
//    {
//      $ERROR('#4.3: obj[0] = -1; obj.length = {valueOf: function() {return 1}, toString: function() {throw "error"}}; obj.pop() not throw Error. Actual: ' + (e));
//    }}
//
//  obj[0] = - 1;
//  obj.length = {
//    toString : (function () 
//    {
//      return 0;
//    })
//  };
//  var pop = obj.pop();
//  {
//    var __result5 = pop !== undefined;
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
//  var pop = obj.pop();
//  {
//    var __result6 = pop !== undefined;
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
//    var pop = obj.pop();
//    $ERROR('#7.1: obj[0] = -1; obj.length = {valueOf: function() {throw "error"}, toString: function() {return 0}}; obj.pop() throw "error". Actual: ' + (pop));}
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
//    var pop = obj.pop();
//    $ERROR('#8.1: obj[0] = -1; obj.length = {valueOf: function() {return {}}, toString: function() {return {}}}  obj.pop() throw TypeError. Actual: ' + (pop));}
//  catch (e)
//{    {
//      var __result8 = (e instanceof TypeError) !== true;
//      var __expect8 = false;
//    }}
//
//  
