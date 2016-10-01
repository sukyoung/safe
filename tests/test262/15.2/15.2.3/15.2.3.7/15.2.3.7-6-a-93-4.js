//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     Object.defineProperty(obj, "0", {
//       value : 1001,
//       writable : false,
//       configurable : false
//     });
//     Object.defineProperty(obj, "1", {
//       value : 1003,
//       writable : false,
//       configurable : true
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
// {      return e instanceof TypeError && dataPropertyAttributesAreCorrect(obj, "0", 1001, false, false, false) && dataPropertyAttributesAreCorrect(obj, "1", 1003, false, false, true);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
