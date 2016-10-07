// Reference error
//   try
// {    1 = 1;
//     $ERROR('#1.1: 1 = 1 throw ReferenceError (or SyntaxError). Actual: ' + (1 = 1));}
//   catch (e)
// {    if ((e instanceof ReferenceError) !== true)
//     {
//       $ERROR('#1.2: 1 = 1 throw ReferenceError (or SyntaxError). Actual: ' + (e));
//     }
//     else
//     {
//       1 = 1;
//     }}

