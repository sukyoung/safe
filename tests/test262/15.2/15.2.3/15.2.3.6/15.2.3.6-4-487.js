//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var verifySetFunc = "data";
//     var setFunc = (function (value) 
//     {
//       verifySetFunc = value;
//     });
//     Object.defineProperty(obj, "prop", {
//       get : undefined,
//       set : setFunc,
//       enumerable : false,
//       configurable : false
//     });
//     var propertyDefineCorrect = obj.hasOwnProperty("prop");
//     var desc = Object.getOwnPropertyDescriptor(obj, "prop");
//     for(var p in obj)
//     {
//       if (p === "prop")
//       {
//         return false;
//       }
//     }
//     return propertyDefineCorrect && desc.enumerable === false;
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
