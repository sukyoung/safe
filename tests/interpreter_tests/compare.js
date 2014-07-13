var arr1 = [1, 2, 3], arr1_pt = arr1;
var arr2 = [1, 2, 3];

_<>_print(1 < 2);
_<>_print(1 <= 2);
_<>_print(1 > 2);
_<>_print(1 >= 2);
_<>_print(1 == 2);
_<>_print(1 != 2);
_<>_print("123" < "456");
_<>_print("123" >= "456");
_<>_print("123" == "123");
_<>_print("123" != "123");
_<>_print(" ");

_<>_print(1 == "1");
_<>_print(1 != "1");
_<>_print(1 === "1");
_<>_print(1 !== "1");
_<>_print("123" === "123");
_<>_print("123" !== "123");
_<>_print(" ");

_<>_print(arr1 == arr1_pt);
_<>_print(arr1 == arr2);
_<>_print(arr2 == arr1_pt);
_<>_print(arr1 === arr1_pt);
_<>_print(arr1 === arr2);
_<>_print(arr2 === arr1_pt);

"PASS"
