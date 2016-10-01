//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     Object.defineProperty(obj, "0", {
//       value : 1001,
//       writable : false,
//       configurable : true
//     });
//     Object.defineProperty(obj, "1", {
//       value : 1003,
//       writable : false,
//       configurable : false
//     });
//     try
// {      Object.defineProperties(obj, {
//         0 : {
//           value : 1002
//         },
//         1 : {
//           value : 1004
//         }
//       });
//       return false;}
//     catch (e)
// {      return e instanceof TypeError && dataPropertyAttributesAreCorrect(obj, "0", 1002, false, false, true) && dataPropertyAttributesAreCorrect(obj, "1", 1003, false, false, false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
