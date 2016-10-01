// TODO [[DefaultValue]]
  var object = {
    valueOf : (function () 
    {
      return "+";
    })
  };
  var x = new Array(object);
  var __result0 = x.toString() !== x.join();
  var __expect0 = false;
    {
      var __result1 = x.toString() !== "[object Object]";
      var __expect1 = false;
    }
  var object = {
    valueOf : (function () 
    {
      return "+";
    }),
    toString : (function () 
    {
      return "*";
    })
  };
  var x = new Array(object);  
  var __result2 = x.toString() !== x.join();
  var __expect2 = false;
    {
      var __result3 = x.toString() !== "*";
      var __expect3 = false;
    }
  var object = {
    valueOf : (function () 
    {
      return "+";
    }),
    toString : (function () 
    {
      return {
        
      };
    })
  };
  var x = new Array(object);
  var __result4 = x.toString() !== x.join();
  var __expect4 = false;
    {
      var __result5 = x.toString() !== "+";
      var __expect5 = false;
    }
  try
{    var object = {
      valueOf : (function () 
      {
        throw "error";
      }),
      toString : (function () 
      {
        return "*";
      })
    };
    var x = new Array(object);
    var __result6 = x.toString() !== x.join();
	var __expect6 = false;
      {
        var __result7 = x.toString() !== "*";
        var __expect7 = false;
      }
    }
  catch (e)
{    
	var __result8 = e === "error"; 
	var __expect8 = false;
	
	}

  var object = {
    toString : (function () 
    {
      return "*";
    })
  };
  var x = new Array(object);
  var __result9 = x.toString() !== x.join();
  var __expect9 = false;
    {
      var __result10 = x.toString() !== "*";
      var __expect10 = false;
    }
  var object = {
    valueOf : (function () 
    {
      return {
        
      };
    }),
    toString : (function () 
    {
      return "*";
    })
  };
  var x = new Array(object);
  var __result11 = x.toString() !== x.join();
  var __expect11 = false;
    {
      var __result12 = x.toString() !== "*";
      var __expect12 = false;
    } 

//   try
// {    var object = {
//       valueOf : (function () 
//       {
//         return "+";
//       }),
//       toString : (function () 
//       {
//         throw "error";
//       })
//     };
//     var x = new Array(object);
//     x.toString();
// }
//   catch (e)
// {    {
//       var __result13 = e !== "error";
//       var __expect13 = false;
//     }}

  try
{    var object = {
      valueOf : (function () 
      {
        return {
          
        };
      }),
      toString : (function () 
      {
        return {
          
        };
      })
    };
    var x = new Array(object);
    x.toString();
    // $ERROR('#8.1: var object = {valueOf: function() {return {}}, toString: function() {return {}}} var x = new Array(object); x.toString() throw TypeError. Actual: ' + (x.toString()));
}
  catch (e)
{    {
      var __result14 = (e instanceof TypeError) !== true;
      var __expect14 = false;
    }}
