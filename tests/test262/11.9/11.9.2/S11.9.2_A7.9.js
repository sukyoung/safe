// TODO [[DefaultValue]]
//   {
//     var __result1 = ({
//       valueOf : (function () 
//       {
//         return 1;
//       })
//     } != true) !== false;
//     var __expect1 = false;
//   }
//   {
//     var __result2 = ({
//       valueOf : (function () 
//       {
//         return 1;
//       }),
//       toString : (function () 
//       {
//         return 0;
//       })
//     } != 1) !== false;
//     var __expect2 = false;
//   }
//   {
//     var __result3 = ({
//       valueOf : (function () 
//       {
//         return 1;
//       }),
//       toString : (function () 
//       {
//         return {
          
//         };
//       })
//     } != "+1") !== false;
//     var __expect3 = false;
//   }
//   try
// {    {
//       var __result4 = ({
//         valueOf : (function () 
//         {
//           return "+1";
//         }),
//         toString : (function () 
//         {
//           throw "error";
//         })
//       } != true) !== false;
//       var __expect4 = false;
//     }}
//   catch (e)
// {    if (e === "error")
//     {
//       $ERROR('#4.2: ({valueOf: function() {return "+1"}, toString: function() {throw "error"}} != true) not throw "error"');
//     }
//     else
//     {
//       $ERROR('#4.3: ({valueOf: function() {return "+1"}, toString: function() {throw "error"}} != true) not throw Error. Actual: ' + (e));
//     }}

//   {
//     var __result5 = ({
//       toString : (function () 
//       {
//         return "+1";
//       })
//     } != 1) !== false;
//     var __expect5 = false;
//   }
//   if (({
//     valueOf : (function () 
//     {
//       return {
        
//       };
//     }),
//     toString : (function () 
//     {
//       return "+1";
//     })
//   } != "1") !== true)
//   {
//     $ERROR('#6.1: ({valueOf: function() {return {}}, toString: function() {return "+1"}} != "1") === true');
//   }
//   else
//   {
//     {
//       var __result6 = ({
//         valueOf : (function () 
//         {
//           return {
            
//           };
//         }),
//         toString : (function () 
//         {
//           return "+1";
//         })
//       } != "+1") !== false;
//       var __expect6 = false;
//     }
//   }
//   try
// {    ({
//       valueOf : (function () 
//       {
//         throw "error";
//       }),
//       toString : (function () 
//       {
//         return 1;
//       })
//     } != 1);
//     $ERROR('#7.1: ({valueOf: function() {throw "error"}, toString: function() {return 1}} != 1) throw "error". Actual: ' + ({
//       valueOf : (function () 
//       {
//         throw "error";
//       }),
//       toString : (function () 
//       {
//         return 1;
//       })
//     } != 1));}
//   catch (e)
// {    {
//       var __result7 = e !== "error";
//       var __expect7 = false;
//     }}

//   try
// {    ({
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
//     } != 1);
//     $ERROR('#8.1: ({valueOf: function() {return {}}, toString: function() {return {}}} != 1) throw TypeError. Actual: ' + ({
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
//     } != 1));}
//   catch (e)
// {    {
//       var __result8 = (e instanceof TypeError) !== true;
//       var __expect8 = false;
//     }}

