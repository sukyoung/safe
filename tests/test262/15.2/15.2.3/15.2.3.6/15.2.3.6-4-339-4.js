//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = (function () 
//     {
//       return arguments;
//     })();
//     Object.defineProperty(obj, "0", {
//       value : 2010,
//       writable : true,
//       enumerable : true,
//       configurable : false
//     });
//     var propertyDefineCorrect = obj.hasOwnProperty("0");
//     var desc1 = Object.getOwnPropertyDescriptor(obj, "0");
//     function getFunc() 
//     {
//       return 20;
//     }
//     try
// {      Object.defineProperty(obj, "0", {
//         get : getFunc
//       });
//       return false;}
//     catch (e)
// {      var desc2 = Object.getOwnPropertyDescriptor(obj, "0");
//       return propertyDefineCorrect && desc1.value === 2010 && obj[0] === 2010 && typeof desc2.get === "undefined" && e instanceof TypeError;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
