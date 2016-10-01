//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var obj1 = {
//       length : 10
//     };
//     Object.defineProperty(obj, "foo", {
//       value : obj1,
//       writable : false,
//       configurable : false
//     });
//     var obj2 = {
//       length : 20
//     };
//     try
// {      Object.defineProperty(obj, "foo", {
//         value : obj2
//       });
//       return false;}
//     catch (e)
// {      return e instanceof TypeError && dataPropertyAttributesAreCorrect(obj, "foo", obj1, false, false, false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
