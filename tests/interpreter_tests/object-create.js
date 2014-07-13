o = { x: 42 };
obj = Object.create(o, { a: { value: 1, configurable: false, enumerable: false, writable: true }, b: { value: 2, configurable: false, enumerable: false, writable: true } });
_<>_print(obj.a);
_<>_print(obj.b);
_<>_print(obj.x);
"PASS"
