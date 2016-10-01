//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var getFunc = (function () 
//     {
//       return 1001;
//     });
//     Object.defineProperty(obj, "prop", {
//       get : getFunc,
//       set : undefined,
//       enumerable : true,
//       configurable : false
//     });
//     var desc1 = Object.getOwnPropertyDescriptor(obj, "prop");
//     try
// {      Object.defineProperty(obj, "prop", {
//         configurable : true
//       });
//       return false;}
//     catch (e)
// {      var desc2 = Object.getOwnPropertyDescriptor(obj, "prop");
//       delete obj.prop;
//       return desc1.configurable === false && desc2.configurable === false && obj.hasOwnProperty("prop") && e instanceof TypeError;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
