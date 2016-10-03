// TODO [[DefineOwnProperty]] for Array object
//  var obj = {
//    
//  };
//  obj.push = Array.prototype.push;
//  obj.length = {
//    valueOf : (function () 
//    {
//      return 3;
//    })
//  };
//  var push = obj.push();
//  {
//    var __result1 = push !== 3;
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
//  var push = obj.push();
//  {
//    var __result2 = push !== 3;
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
//  var push = obj.push();
//  {
//    var __result3 = push !== 3;
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
//    var push = obj.push();
//    {
//      var __result4 = push !== 3;
//      var __expect4 = false;
//    }}
//  catch (e)
//{    if (e === "error")
//    {
//      $ERROR('#4.2:  obj.length = {valueOf: function() {return 3}, toString: function() {throw "error"}}; obj.push() not throw "error"');
//    }
//    else
//    {
//      $ERROR('#4.3:  obj.length = {valueOf: function() {return 3}, toString: function() {throw "error"}}; obj.push() not throw Error. Actual: ' + (e));
//    }}
//
//  obj.length = {
//    toString : (function () 
//    {
//      return 1;
//    })
//  };
//  var push = obj.push();
//  {
//    var __result5 = push !== 1;
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
//  var push = obj.push();
//  {
//    var __result6 = push !== 1;
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
//    var push = obj.push();
//    $ERROR('#7.1:  obj.length = {valueOf: function() {throw "error"}, toString: function() {return 1}}; obj.push() throw "error". Actual: ' + (push));}
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
//    var push = obj.push();
//    $ERROR('#8.1:  obj.length = {valueOf: function() {return {}}, toString: function() {return {}}}  obj.push() throw TypeError. Actual: ' + (push));}
//  catch (e)
//{    {
//      var __result8 = (e instanceof TypeError) !== true;
//      var __expect8 = false;
//    }}
//
//  
