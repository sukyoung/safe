function cmp(x, y) {
    if (x < y) return 1;
    if (x > y) return -1;
    return 0;
}

a = [3,1,2]
_<>_print(a.toString())
a.sort(cmp)
_<>_print(a.toString())
a = [3,1,2]
delete a[1]
_<>_print(a.toString())
a.sort(cmp)
_<>_print(a.toString())
Array.prototype[2] = 0
_<>_print(a.toString())
a = [3,1,2]
delete a[2]
_<>_print(a.toString())
a.sort(cmp)
_<>_print(a.toString())
