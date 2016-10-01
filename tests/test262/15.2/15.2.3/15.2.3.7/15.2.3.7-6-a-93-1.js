//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     Object.defineProperty(obj, "property", {
//       value : 1001,
//       writable : false,
//       configurable : true
//     });
//     Object.defineProperty(obj, "property1", {
//       value : 1003,
//       writable : false,
//       configurable : false
//     });
//     try
// {      Object.defineProperties(obj, {
//         property : {
//           value : 1002
//         },
//         property1 : {
//           value : 1004
//         }
//       });
//       return false;}
//     catch (e)
// {      return e instanceof TypeError && dataPropertyAttributesAreCorrect(obj, "property", 1002, false, false, true) && dataPropertyAttributesAreCorrect(obj, "property1", 1003, false, false, false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
