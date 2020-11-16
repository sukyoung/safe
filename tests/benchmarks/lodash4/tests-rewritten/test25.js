QUnit.module('clone methods');
(function () {
    function Foo() {
        this.a = __num_top__;
    }
    Foo.prototype.b = __num_top__;
    Foo.c = function () {
    };
    if (Map) {
        var map = new Map();
        map.set(__str_top__, __num_top__);
        map.set(__str_top__, __num_top__);
    }
    if (Set) {
        var set = new Set();
        set.add(__num_top__);
        set.add(__num_top__);
    }
    var objects = {
        '`arguments` objects': arguments,
        'arrays': [
            __str_top__,
            __str_top__
        ],
        'array-like objects': {
            '0': __str_top__,
            'length': __num_top__
        },
        'booleans': __bool_top__,
        'boolean objects': Object(__bool_top__),
        'date objects': new Date(),
        'Foo instances': new Foo(),
        'objects': {
            'a': __num_top__,
            'b': __num_top__,
            'c': __num_top__
        },
        'objects with object values': {
            'a': /a/,
            'b': [__str_top__],
            'c': { 'C': __num_top__ }
        },
        'objects from another document': realm.object || {},
        'maps': map,
        'null values': null,
        'numbers': __num_top__,
        'number objects': Object(__num_top__),
        'regexes': /a/gim,
        'sets': set,
        'strings': __str_top__,
        'string objects': Object(__str_top__),
        'undefined values': undefined
    };
    objects.arrays.length = __num_top__;
    var uncloneable = {
        'DOM elements': body,
        'functions': Foo,
        'async functions': asyncFunc,
        'generator functions': genFunc,
        'the `Proxy` constructor': Proxy
    };
    lodashStable.each(errors, function (error) {
        uncloneable[error.name + __str_top__] = error;
    });
    QUnit.test('`_.clone` should perform a shallow clone', function (assert) {
        assert.expect(2);
        var array = [
                { 'a': __num_top__ },
                { 'b': __num_top__ }
            ], actual = _.clone(array);
        assert.deepEqual(actual, array);
        assert.ok(actual !== array && actual[__num_top__] === array[__num_top__]);
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
        lodashStable.times(LARGE_ARRAY_SIZE + __num_top__, function (index) {
            cyclical[__str_top__ + index] = [index ? cyclical[__str_top__ + (index - __num_top__)] : cyclical];
        });
        var clone = _.cloneDeep(cyclical), actual = clone[__str_top__ + LARGE_ARRAY_SIZE][__num_top__];
        assert.strictEqual(actual, clone[__str_top__ + (LARGE_ARRAY_SIZE - __num_top__)]);
        assert.notStrictEqual(actual, cyclical[__str_top__ + (LARGE_ARRAY_SIZE - __num_top__)]);
    });
    QUnit.test('`_.cloneDeepWith` should provide `stack` to `customizer`', function (assert) {
        assert.expect(1);
        var actual;
        _.cloneDeepWith({ 'a': __num_top__ }, function () {
            actual = _.last(arguments);
        });
        assert.ok(isNpm ? actual.constructor.name == __str_top__ : actual instanceof mapCaches.Stack);
    });
    lodashStable.each([
        __str_top__,
        __str_top__
    ], function (methodName) {
        var func = _[methodName], isDeep = methodName == __str_top__;
        lodashStable.forOwn(objects, function (object, kind) {
            QUnit.test(__str_top__ + methodName + __str_top__ + kind, function (assert) {
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
        QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
            assert.expect(2);
            if (ArrayBuffer) {
                var actual = func(arrayBuffer);
                assert.strictEqual(actual.byteLength, arrayBuffer.byteLength);
                assert.notStrictEqual(actual, arrayBuffer);
            } else {
                skipAssert(assert, 2);
            }
        });
        QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
            assert.expect(4);
            if (Buffer) {
                var buffer = new Buffer([
                        __num_top__,
                        __num_top__
                    ]), actual = func(buffer);
                assert.strictEqual(actual.byteLength, buffer.byteLength);
                assert.strictEqual(actual.inspect(), buffer.inspect());
                assert.notStrictEqual(actual, buffer);
                buffer[__num_top__] = __num_top__;
                assert.strictEqual(actual[__num_top__], isDeep ? __num_top__ : __num_top__);
            } else {
                skipAssert(assert, 4);
            }
        });
        QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
            assert.expect(2);
            var array = /c/.exec(__str_top__), actual = func(array);
            assert.strictEqual(actual.index, __num_top__);
            assert.strictEqual(actual.input, __str_top__);
        });
        QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
            assert.expect(1);
            var regexp = /c/g;
            regexp.exec(__str_top__);
            assert.strictEqual(func(regexp).lastIndex, __num_top__);
        });
        QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
            assert.expect(1);
            var values = lodashStable.map([
                __bool_top__,
                __bool_top__,
                __num_top__,
                __str_top__
            ], function (value) {
                var object = Object(value);
                object.a = __num_top__;
                return object;
            });
            var expected = lodashStable.map(values, stubTrue);
            var actual = lodashStable.map(values, function (value) {
                return func(value).a === __num_top__;
            });
            assert.deepEqual(actual, expected);
        });
        QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
            assert.expect(2);
            var actual = func(Foo.prototype);
            assert.notOk(actual instanceof Foo);
            assert.deepEqual(actual, { 'b': __num_top__ });
        });
        QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
            assert.expect(1);
            assert.ok(func(new Foo()) instanceof Foo);
        });
        QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
            assert.expect(1);
            Foo.prototype.constructor = Object;
            assert.ok(func(new Foo()) instanceof Foo);
            Foo.prototype.constructor = Foo;
        });
        QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
            assert.expect(1);
            Foo.prototype.constructor = null;
            assert.notOk(func(new Foo()) instanceof Foo);
            Foo.prototype.constructor = Foo;
        });
        QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
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
        QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
            assert.expect(7);
            function Foo() {
                this[symbol] = { 'c': __num_top__ };
            }
            if (Symbol) {
                var symbol2 = Symbol(__str_top__);
                Foo.prototype[symbol2] = __num_top__;
                var symbol3 = Symbol(__str_top__);
                defineProperty(Foo.prototype, symbol3, {
                    'configurable': __bool_top__,
                    'enumerable': __bool_top__,
                    'writable': __bool_top__,
                    'value': __num_top__
                });
                var object = { 'a': { 'b': new Foo() } };
                object[symbol] = { 'b': __num_top__ };
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
        QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
            assert.expect(4);
            if (Symbol) {
                assert.strictEqual(func(symbol), symbol);
                var object = Object(symbol), actual = func(object);
                assert.strictEqual(typeof actual, __str_top__);
                assert.strictEqual(typeof actual.valueOf(), __str_top__);
                assert.notStrictEqual(actual, object);
            } else {
                skipAssert(assert, 4);
            }
        });
        QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
            assert.expect(1);
            if (Symbol) {
                assert.strictEqual(func(symbol), symbol);
            } else {
                skipAssert(assert);
            }
        });
        QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
            assert.expect(1);
            if (document) {
                var element = document.createElement(__str_top__);
                try {
                    assert.deepEqual(func(element), {});
                } catch (e) {
                    assert.ok(__bool_top__, e.message);
                }
            } else {
                skipAssert(assert);
            }
        });
        QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
            assert.expect(1);
            var props = [];
            var objects = lodashStable.transform(_, function (result, value, key) {
                if (lodashStable.startsWith(key, __str_top__) && lodashStable.isObject(value) && !lodashStable.isArguments(value) && !lodashStable.isElement(value) && !lodashStable.isFunction(value)) {
                    props.push(lodashStable.capitalize(lodashStable.camelCase(key)));
                    result.push(value);
                }
            }, []);
            var expected = lodashStable.map(objects, stubTrue);
            var actual = lodashStable.map(objects, function (object) {
                var Ctor = object.constructor, result = func(object);
                return result !== object && (result instanceof Ctor || !(new Ctor() instanceof Ctor));
            });
            assert.deepEqual(actual, expected, props.join(__str_top__));
        });
        QUnit.test(__str_top__ + methodName + __str_top__ + (isDeep ? __str_top__ : __str_top__) + __str_top__, function (assert) {
            assert.expect(2);
            var expected = [
                    { 'a': [__num_top__] },
                    { 'b': [__num_top__] }
                ], actual = lodashStable.map(expected, func);
            assert.deepEqual(actual, expected);
            if (isDeep) {
                assert.ok(actual[__num_top__] !== expected[__num_top__] && actual[__num_top__].a !== expected[__num_top__].a && actual[__num_top__].b !== expected[__num_top__].b);
            } else {
                assert.ok(actual[__num_top__] !== expected[__num_top__] && actual[__num_top__].a === expected[__num_top__].a && actual[__num_top__].b === expected[__num_top__].b);
            }
        });
        QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
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
            QUnit.test(__str_top__ + methodName + __str_top__ + type + __str_top__, function (assert) {
                assert.expect(10);
                var Ctor = root[type];
                lodashStable.times(__num_top__, function (index) {
                    if (Ctor) {
                        var buffer = new ArrayBuffer(__num_top__), view = index ? new Ctor(buffer, __num_top__, __num_top__) : new Ctor(buffer), actual = func(view);
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
            QUnit.test(__str_top__ + methodName + __str_top__ + key, function (assert) {
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
        __str_top__,
        __str_top__
    ], function (methodName) {
        var func = _[methodName], isDeep = methodName == __str_top__;
        QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
            assert.expect(1);
            var argsList = [], object = new Foo();
            func(object, function () {
                var length = arguments.length, args = slice.call(arguments, __num_top__, length - (length > __num_top__ ? __num_top__ : __num_top__));
                argsList.push(args);
            });
            assert.deepEqual(argsList, isDeep ? [
                [object],
                [
                    __num_top__,
                    __str_top__,
                    object
                ]
            ] : [[object]]);
        });
        QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
            assert.expect(1);
            var actual = func({ 'a': { 'b': __str_top__ } }, noop);
            assert.deepEqual(actual, { 'a': { 'b': __str_top__ } });
        });
        lodashStable.forOwn(uncloneable, function (value, key) {
            QUnit.test(__str_top__ + methodName + __str_top__ + key, function (assert) {
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