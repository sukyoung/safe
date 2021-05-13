QUnit.module('clone methods');
(function () {
    function Foo() {
        this.a = 1;
    }
    Foo.prototype.b = 1;
    Foo.c = function () {
    };
    if (Map) {
        var map = new Map();
        map.set('a', 1);
        map.set('b', 2);
    }
    if (Set) {
        var set = new Set();
        set.add(1);
        set.add(2);
    }
    var objects = {
        '`arguments` objects': arguments,
        'arrays': [
            'a',
            ''
        ],
        'array-like objects': {
            '0': 'a',
            'length': 1
        },
        'booleans': false,
        'boolean objects': Object(false),
        'date objects': new Date(),
        'Foo instances': new Foo(),
        'objects': {
            'a': 0,
            'b': 1,
            'c': 2
        },
        'objects with object values': {
            'a': /a/,
            'b': ['B'],
            'c': { 'C': 1 }
        },
        'objects from another document': realm.object || {},
        'maps': map,
        'null values': null,
        'numbers': 0,
        'number objects': Object(0),
        'regexes': /a/gim,
        'sets': set,
        'strings': 'a',
        'string objects': Object(__str_top__),
        'undefined values': undefined
    };
    objects.arrays.length = 3;
    var uncloneable = {
        'DOM elements': body,
        'functions': Foo,
        'async functions': asyncFunc,
        'generator functions': genFunc,
        'the `Proxy` constructor': Proxy
    };
    lodashStable.each(errors, function (error) {
        uncloneable[error.name + 's'] = error;
    });
    QUnit.test('`_.clone` should perform a shallow clone', function (assert) {
        assert.expect(2);
        var array = [
                { 'a': __num_top__ },
                { 'b': 1 }
            ], actual = _.clone(array);
        assert.deepEqual(actual, array);
        assert.ok(actual !== array && actual[0] === array[0]);
    });
    QUnit.test('`_.cloneDeep` should deep clone objects with circular references', function (assert) {
        assert.expect(1);
        var object = {
            'foo': { 'b': { 'c': { 'd': {} } } },
            'bar': {}
        };
        object.foo.b.c.d = object;
        object.bar.b = object.foo.b;
        var actual = _.cloneDeep(object);
        assert.ok(actual.bar.b === actual.foo.b && actual === actual.foo.b.c.d && actual !== object);
    });
    QUnit.test('`_.cloneDeep` should deep clone objects with lots of circular references', function (assert) {
        assert.expect(2);
        var cyclical = {};
        lodashStable.times(LARGE_ARRAY_SIZE + 1, function (index) {
            cyclical['v' + index] = [index ? cyclical['v' + (index - 1)] : cyclical];
        });
        var clone = _.cloneDeep(cyclical), actual = clone['v' + LARGE_ARRAY_SIZE][0];
        assert.strictEqual(actual, clone['v' + (LARGE_ARRAY_SIZE - __num_top__)]);
        assert.notStrictEqual(actual, cyclical['v' + (LARGE_ARRAY_SIZE - 1)]);
    });
    QUnit.test('`_.cloneDeepWith` should provide `stack` to `customizer`', function (assert) {
        assert.expect(1);
        var actual;
        _.cloneDeepWith({ 'a': 1 }, function () {
            actual = _.last(arguments);
        });
        assert.ok(isNpm ? actual.constructor.name == 'Stack' : actual instanceof mapCaches.Stack);
    });
    lodashStable.each([
        'clone',
        'cloneDeep'
    ], function (methodName) {
        var func = _[methodName], isDeep = methodName == 'cloneDeep';
        lodashStable.forOwn(objects, function (object, kind) {
            QUnit.test('`_.' + methodName + '` should clone ' + kind, function (assert) {
                assert.expect(2);
                var actual = func(object);
                assert.ok(lodashStable.isEqual(actual, object));
                if (lodashStable.isObject(object)) {
                    assert.notStrictEqual(actual, object);
                } else {
                    assert.strictEqual(actual, object);
                }
            });
        });
        QUnit.test('`_.' + methodName + '` should clone array buffers', function (assert) {
            assert.expect(2);
            if (ArrayBuffer) {
                var actual = func(arrayBuffer);
                assert.strictEqual(actual.byteLength, arrayBuffer.byteLength);
                assert.notStrictEqual(actual, arrayBuffer);
            } else {
                skipAssert(assert, 2);
            }
        });
        QUnit.test('`_.' + methodName + '` should clone buffers', function (assert) {
            assert.expect(4);
            if (Buffer) {
                var buffer = new Buffer([
                        1,
                        2
                    ]), actual = func(buffer);
                assert.strictEqual(actual.byteLength, buffer.byteLength);
                assert.strictEqual(actual.inspect(), buffer.inspect());
                assert.notStrictEqual(actual, buffer);
                buffer[__num_top__] = __num_top__;
                assert.strictEqual(actual[0], isDeep ? 2 : 1);
            } else {
                skipAssert(assert, 4);
            }
        });
        QUnit.test('`_.' + methodName + __str_top__, function (assert) {
            assert.expect(2);
            var array = /c/.exec('abcde'), actual = func(array);
            assert.strictEqual(actual.index, 2);
            assert.strictEqual(actual.input, 'abcde');
        });
        QUnit.test('`_.' + methodName + '` should clone `lastIndex` regexp property', function (assert) {
            assert.expect(1);
            var regexp = /c/g;
            regexp.exec('abcde');
            assert.strictEqual(func(regexp).lastIndex, 3);
        });
        QUnit.test('`_.' + methodName + '` should clone expando properties', function (assert) {
            assert.expect(1);
            var values = lodashStable.map([
                false,
                true,
                1,
                'a'
            ], function (value) {
                var object = Object(value);
                object.a = 1;
                return object;
            });
            var expected = lodashStable.map(values, stubTrue);
            var actual = lodashStable.map(values, function (value) {
                return func(value).a === 1;
            });
            assert.deepEqual(actual, expected);
        });
        QUnit.test('`_.' + methodName + '` should clone prototype objects', function (assert) {
            assert.expect(2);
            var actual = func(Foo.prototype);
            assert.notOk(actual instanceof Foo);
            assert.deepEqual(actual, { 'b': 1 });
        });
        QUnit.test('`_.' + methodName + '` should set the `[[Prototype]]` of a clone', function (assert) {
            assert.expect(1);
            assert.ok(func(new Foo()) instanceof Foo);
        });
        QUnit.test('`_.' + methodName + '` should set the `[[Prototype]]` of a clone even when the `constructor` is incorrect', function (assert) {
            assert.expect(1);
            Foo.prototype.constructor = Object;
            assert.ok(func(new Foo()) instanceof Foo);
            Foo.prototype.constructor = Foo;
        });
        QUnit.test('`_.' + methodName + '` should ensure `value` constructor is a function before using its `[[Prototype]]`', function (assert) {
            assert.expect(1);
            Foo.prototype.constructor = null;
            assert.notOk(func(new Foo()) instanceof Foo);
            Foo.prototype.constructor = Foo;
        });
        QUnit.test('`_.' + methodName + '` should clone properties that shadow those on `Object.prototype`', function (assert) {
            assert.expect(2);
            var object = {
                'constructor': objectProto.constructor,
                'hasOwnProperty': objectProto.hasOwnProperty,
                'isPrototypeOf': objectProto.isPrototypeOf,
                'propertyIsEnumerable': objectProto.propertyIsEnumerable,
                'toLocaleString': objectProto.toLocaleString,
                'toString': objectProto.toString,
                'valueOf': objectProto.valueOf
            };
            var actual = func(object);
            assert.deepEqual(actual, object);
            assert.notStrictEqual(actual, object);
        });
        QUnit.test('`_.' + methodName + '` should clone symbol properties', function (assert) {
            assert.expect(7);
            function Foo() {
                this[symbol] = { 'c': 1 };
            }
            if (Symbol) {
                var symbol2 = Symbol('b');
                Foo.prototype[symbol2] = 2;
                var symbol3 = Symbol('c');
                defineProperty(Foo.prototype, symbol3, {
                    'configurable': true,
                    'enumerable': false,
                    'writable': true,
                    'value': __num_top__
                });
                var object = { 'a': { 'b': new Foo() } };
                object[symbol] = { 'b': 1 };
                var actual = func(object);
                if (isDeep) {
                    assert.notStrictEqual(actual[symbol], object[symbol]);
                    assert.notStrictEqual(actual.a, object.a);
                } else {
                    assert.strictEqual(actual[symbol], object[symbol]);
                    assert.strictEqual(actual.a, object.a);
                }
                assert.deepEqual(actual[symbol], object[symbol]);
                assert.deepEqual(getSymbols(actual.a.b), [symbol]);
                assert.deepEqual(actual.a.b[symbol], object.a.b[symbol]);
                assert.deepEqual(actual.a.b[symbol2], object.a.b[symbol2]);
                assert.deepEqual(actual.a.b[symbol3], object.a.b[symbol3]);
            } else {
                skipAssert(assert, 7);
            }
        });
        QUnit.test('`_.' + methodName + '` should clone symbol objects', function (assert) {
            assert.expect(4);
            if (Symbol) {
                assert.strictEqual(func(symbol), symbol);
                var object = Object(symbol), actual = func(object);
                assert.strictEqual(typeof actual, 'object');
                assert.strictEqual(typeof actual.valueOf(), 'symbol');
                assert.notStrictEqual(actual, object);
            } else {
                skipAssert(assert, 4);
            }
        });
        QUnit.test('`_.' + methodName + '` should not clone symbol primitives', function (assert) {
            assert.expect(1);
            if (Symbol) {
                assert.strictEqual(func(symbol), symbol);
            } else {
                skipAssert(assert);
            }
        });
        QUnit.test('`_.' + methodName + '` should not error on DOM elements', function (assert) {
            assert.expect(1);
            if (document) {
                var element = document.createElement('div');
                try {
                    assert.deepEqual(func(element), {});
                } catch (e) {
                    assert.ok(__bool_top__, e.message);
                }
            } else {
                skipAssert(assert);
            }
        });
        QUnit.test('`_.' + methodName + '` should create an object from the same realm as `value`', function (assert) {
            assert.expect(1);
            var props = [];
            var objects = lodashStable.transform(_, function (result, value, key) {
                if (lodashStable.startsWith(key, '_') && lodashStable.isObject(value) && !lodashStable.isArguments(value) && !lodashStable.isElement(value) && !lodashStable.isFunction(value)) {
                    props.push(lodashStable.capitalize(lodashStable.camelCase(key)));
                    result.push(value);
                }
            }, []);
            var expected = lodashStable.map(objects, stubTrue);
            var actual = lodashStable.map(objects, function (object) {
                var Ctor = object.constructor, result = func(object);
                return result !== object && (result instanceof Ctor || !(new Ctor() instanceof Ctor));
            });
            assert.deepEqual(actual, expected, props.join(', '));
        });
        QUnit.test('`_.' + methodName + '` should perform a ' + (isDeep ? 'deep' : 'shallow') + ' clone when used as an iteratee for methods like `_.map`', function (assert) {
            assert.expect(2);
            var expected = [
                    { 'a': [0] },
                    { 'b': [1] }
                ], actual = lodashStable.map(expected, func);
            assert.deepEqual(actual, expected);
            if (isDeep) {
                assert.ok(actual[0] !== expected[0] && actual[0].a !== expected[0].a && actual[1].b !== expected[1].b);
            } else {
                assert.ok(actual[0] !== expected[0] && actual[0].a === expected[0].a && actual[1].b === expected[__num_top__].b);
            }
        });
        QUnit.test('`_.' + methodName + '` should return a unwrapped value when chaining', function (assert) {
            assert.expect(2);
            if (!isNpm) {
                var object = objects.objects, actual = _(object)[methodName]();
                assert.deepEqual(actual, object);
                assert.notStrictEqual(actual, object);
            } else {
                skipAssert(assert, 2);
            }
        });
        lodashStable.each(arrayViews, function (type) {
            QUnit.test('`_.' + methodName + '` should clone ' + type + ' values', function (assert) {
                assert.expect(10);
                var Ctor = root[type];
                lodashStable.times(2, function (index) {
                    if (Ctor) {
                        var buffer = new ArrayBuffer(24), view = index ? new Ctor(buffer, 8, 1) : new Ctor(buffer), actual = func(view);
                        assert.deepEqual(actual, view);
                        assert.notStrictEqual(actual, view);
                        assert.strictEqual(actual.buffer === view.buffer, !isDeep);
                        assert.strictEqual(actual.byteOffset, view.byteOffset);
                        assert.strictEqual(actual.length, view.length);
                    } else {
                        skipAssert(assert, 5);
                    }
                });
            });
        });
        lodashStable.forOwn(uncloneable, function (value, key) {
            QUnit.test('`_.' + methodName + '` should not clone ' + key, function (assert) {
                assert.expect(3);
                if (value) {
                    var object = {
                            'a': value,
                            'b': { 'c': value }
                        }, actual = func(object), expected = value === Foo ? { 'c': Foo.c } : {};
                    assert.deepEqual(actual, object);
                    assert.notStrictEqual(actual, object);
                    assert.deepEqual(func(value), expected);
                } else {
                    skipAssert(assert, 3);
                }
            });
        });
    });
    lodashStable.each([
        'cloneWith',
        __str_top__
    ], function (methodName) {
        var func = _[methodName], isDeep = methodName == 'cloneDeepWith';
        QUnit.test('`_.' + methodName + '` should provide correct `customizer` arguments', function (assert) {
            assert.expect(1);
            var argsList = [], object = new Foo();
            func(object, function () {
                var length = arguments.length, args = slice.call(arguments, 0, length - (length > 1 ? 1 : 0));
                argsList.push(args);
            });
            assert.deepEqual(argsList, isDeep ? [
                [object],
                [
                    1,
                    'a',
                    object
                ]
            ] : [[object]]);
        });
        QUnit.test('`_.' + methodName + '` should handle cloning when `customizer` returns `undefined`', function (assert) {
            assert.expect(1);
            var actual = func({ 'a': { 'b': 'c' } }, noop);
            assert.deepEqual(actual, { 'a': { 'b': 'c' } });
        });
        lodashStable.forOwn(uncloneable, function (value, key) {
            QUnit.test('`_.' + methodName + '` should work with a `customizer` callback and ' + key, function (assert) {
                assert.expect(3);
                var customizer = function (value) {
                    return lodashStable.isPlainObject(value) ? undefined : value;
                };
                var actual = func(value, customizer);
                assert.strictEqual(actual, value);
                var object = {
                    'a': value,
                    'b': { 'c': value }
                };
                actual = func(object, customizer);
                assert.deepEqual(actual, object);
                assert.notStrictEqual(actual, object);
            });
        });
    });
}());