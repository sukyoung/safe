// TODO [[DefaultValue]]
//   var object = {
//     valueOf : (function () 
//     {
//       return 1;
//     })
//   };
//   var y = object++;
//   if (y !== 1)
//   {
//     $ERROR('#1: var object = {valueOf: function() {return 1}}; var y = object++; y === 1. Actual: ' + (y));
//   }
//   else
//   {
//     {
//       var __result1 = object !== 1 + 1;
//       var __expect1 = false;
//     }
//   }
//   var object = {
//     valueOf : (function () 
//     {
//       return 1;
//     }),
//     toString : (function () 
//     {
//       return 0;
//     })
//   };
//   var y = object++;
//   if (y !== 1)
//   {
//     $ERROR('#2: var object = {valueOf: function() {return 1}, toString: function() {return 0}}; var y = object++; y === 1. Actual: ' + (y));
//   }
//   else
//   {
//     {
//       var __result2 = object !== 1 + 1;
//       var __expect2 = false;
//     }
//   }
//   var object = {
//     valueOf : (function () 
//     {
//       return 1;
//     }),
//     toString : (function () 
//     {
//       return {
        
//       };
//     })
//   };
//   var y = object++;
//   if (y !== 1)
//   {
//     $ERROR('#3: var object = {valueOf: function() {return 1}, toString: function() {return {}}}; var y = object++; y === 1. Actual: ' + (y));
//   }
//   else
//   {
//     {
//       var __result3 = object !== 1 + 1;
//       var __expect3 = false;
//     }
//   }
//   try
// {    var object = {
//       valueOf : (function () 
//       {
//         return 1;
//       }),
//       toString : (function () 
//       {
//         throw "error";
//       })
//     };
//     var y = object++;
//     if (y !== 1)
//     {
//       $ERROR('#4.1: var object = {valueOf: function() {return 1}, toString: function() {throw "error"}}; var y = object++; y === 1. Actual: ' + (y));
//     }
//     else
//     {
//       {
//         var __result4 = object !== 1 + 1;
//         var __expect4 = false;
//       }
//     }}
//   catch (e)
// {    if (e === "error")
//     {
//       $ERROR('#4.3: var object = {valueOf: function() {return 1}, toString: function() {throw "error"}}; var y = object++; y not throw "error"');
//     }
//     else
//     {
//       $ERROR('#4.4: var object = {valueOf: function() {return 1}, toString: function() {throw "error"}}; var y = object++; y not throw Error. Actual: ' + (e));
//     }}

//   var object = {
//     toString : (function () 
//     {
//       return 1;
//     })
//   };
//   var y = object++;
//   if (y !== 1)
//   {
//     $ERROR('#5.1: var object = {toString: function() {return 1}}; var y = object++; y === 1. Actual: ' + (y));
//   }
//   else
//   {
//     {
//       var __result5 = object !== 1 + 1;
//       var __expect5 = false;
//     }
//   }
//   var object = {
//     valueOf : (function () 
//     {
//       return {
        
//       };
//     }),
//     toString : (function () 
//     {
//       return 1;
//     })
//   };
//   var y = object++;
//   if (y !== 1)
//   {
//     $ERROR('#6.1: var object = {valueOf: function() {return {}}, toString: function() {return 1}}; var y = object++; y === 1. Actual: ' + (y));
//   }
//   else
//   {
//     {
//       var __result6 = object !== 1 + 1;
//       var __expect6 = false;
//     }
//   }
//   try
// {    var object = {
//       valueOf : (function () 
//       {
//         throw "error";
//       }),
//       toString : (function () 
//       {
//         return 1;
//       })
//     };
//     var y = object++;
//     $ERROR('#7.1: var object = {valueOf: function() {throw "error"}, toString: function() {return 1}}; object++ throw "error". Actual: ' + (y));}
//   catch (e)
// {    {
//       var __result7 = e !== "error";
//       var __expect7 = false;
//     }}

//   try
// {    var object = {
//       valueOf : (function () 
//       {
//         return {
          
//         };
//       }),
//       toString : (function () 
//       {
//         return {
          
//         };
//       })
//     };
//     var y = object++;
//     $ERROR('#8.1: var object = {valueOf: function() {return {}}, toString: function() {return {}}}; object++ throw TypeError. Actual: ' + (y));}
//   catch (e)
// {    {
//       var __result8 = (e instanceof TypeError) !== true;
//       var __expect8 = false;
//     }}

  
