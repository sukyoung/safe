QUnit.module('lodash.bind');
(function () {
    function fn() {
        var result = [this];
        push.apply(result, arguments);
        return result;
    }
    QUnit.test('should bind a function to an object', function (assert) {
        assert.expect(1);
        var object = {}, bound = _.bind(fn, object);
        assert.deepEqual(bound(__str_top__), [
            object,
            __str_top__
        ]);
    });
    QUnit.test('should accept a falsey `thisArg`', function (assert) {
        assert.expect(1);
        var values = lodashStable.reject(falsey.slice(__num_top__), function (value) {
                return value == null;
            }), expected = lodashStable.map(values, function (value) {
                return [value];
            });
        var actual = lodashStable.map(values, function (value) {
            try {
                var bound = _.bind(fn, value);
                return bound();
            } catch (e) {
            }
        });
        assert.ok(lodashStable.every(actual, function (value, index) {
            return lodashStable.isEqual(value, expected[index]);
        }));
    });
    QUnit.test('should bind a function to nullish values', function (assert) {
        assert.expect(6);
        var bound = _.bind(fn, null), actual = bound(__str_top__);
        assert.ok(actual[__num_top__] === null || actual[__num_top__] && actual[__num_top__].Array);
        assert.strictEqual(actual[__num_top__], __str_top__);
        lodashStable.times(__num_top__, function (index) {
            bound = index ? _.bind(fn, undefined) : _.bind(fn);
            actual = bound(__str_top__);
            assert.ok(actual[__num_top__] === undefined || actual[__num_top__] && actual[__num_top__].Array);
            assert.strictEqual(actual[__num_top__], __str_top__);
        });
    });
    QUnit.test('should partially apply arguments ', function (assert) {
        assert.expect(4);
        var object = {}, bound = _.bind(fn, object, __str_top__);
        assert.deepEqual(bound(), [
            object,
            __str_top__
        ]);
        bound = _.bind(fn, object, __str_top__);
        assert.deepEqual(bound(__str_top__), [
            object,
            __str_top__,
            __str_top__
        ]);
        bound = _.bind(fn, object, __str_top__, __str_top__);
        assert.deepEqual(bound(), [
            object,
            __str_top__,
            __str_top__
        ]);
        assert.deepEqual(bound(__str_top__, __str_top__), [
            object,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test('should support placeholders', function (assert) {
        assert.expect(4);
        var object = {}, ph = _.bind.placeholder, bound = _.bind(fn, object, ph, __str_top__, ph);
        assert.deepEqual(bound(__str_top__, __str_top__), [
            object,
            __str_top__,
            __str_top__,
            __str_top__
        ]);
        assert.deepEqual(bound(__str_top__), [
            object,
            __str_top__,
            __str_top__,
            undefined
        ]);
        assert.deepEqual(bound(__str_top__, __str_top__, __str_top__), [
            object,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__
        ]);
        assert.deepEqual(bound(), [
            object,
            undefined,
            __str_top__,
            undefined
        ]);
    });
    QUnit.test('should use `_.placeholder` when set', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            var _ph = _.placeholder = {}, ph = _.bind.placeholder, object = {}, bound = _.bind(fn, object, _ph, __str_top__, ph);
            assert.deepEqual(bound(__str_top__, __str_top__), [
                object,
                __str_top__,
                __str_top__,
                ph,
                __str_top__
            ]);
            delete _.placeholder;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should create a function with a `length` of `0`', function (assert) {
        assert.expect(2);
        var fn = function (a, b, c) {
            }, bound = _.bind(fn, {});
        assert.strictEqual(bound.length, __num_top__);
        bound = _.bind(fn, {}, __num_top__);
        assert.strictEqual(bound.length, __num_top__);
    });
    QUnit.test('should ignore binding when called with the `new` operator', function (assert) {
        assert.expect(3);
        function Foo() {
            return this;
        }
        var bound = _.bind(Foo, { 'a': __num_top__ }), newBound = new bound();
        assert.strictEqual(bound().a, __num_top__);
        assert.strictEqual(newBound.a, undefined);
        assert.ok(newBound instanceof Foo);
    });
    QUnit.test('should handle a number of arguments when called with the `new` operator', function (assert) {
        assert.expect(1);
        function Foo() {
            return this;
        }
        function Bar() {
        }
        var thisArg = { 'a': __num_top__ }, boundFoo = _.bind(Foo, thisArg), boundBar = _.bind(Bar, thisArg), count = __num_top__, expected = lodashStable.times(count, lodashStable.constant([
                undefined,
                undefined
            ]));
        var actual = lodashStable.times(count, function (index) {
            try {
                switch (index) {
                case __num_top__:
                    return [
                        new boundFoo().a,
                        new boundBar().a
                    ];
                case __num_top__:
                    return [
                        new boundFoo(__num_top__).a,
                        new boundBar(__num_top__).a
                    ];
                case __num_top__:
                    return [
                        new boundFoo(__num_top__, __num_top__).a,
                        new boundBar(__num_top__, __num_top__).a
                    ];
                case __num_top__:
                    return [
                        new boundFoo(__num_top__, __num_top__, __num_top__).a,
                        new boundBar(__num_top__, __num_top__, __num_top__).a
                    ];
                case __num_top__:
                    return [
                        new boundFoo(__num_top__, __num_top__, __num_top__, __num_top__).a,
                        new boundBar(__num_top__, __num_top__, __num_top__, __num_top__).a
                    ];
                case __num_top__:
                    return [
                        new boundFoo(__num_top__, __num_top__, __num_top__, __num_top__, __num_top__).a,
                        new boundBar(__num_top__, __num_top__, __num_top__, __num_top__, __num_top__).a
                    ];
                case __num_top__:
                    return [
                        new boundFoo(__num_top__, __num_top__, __num_top__, __num_top__, __num_top__, __num_top__).a,
                        new boundBar(__num_top__, __num_top__, __num_top__, __num_top__, __num_top__, __num_top__).a
                    ];
                case __num_top__:
                    return [
                        new boundFoo(__num_top__, __num_top__, __num_top__, __num_top__, __num_top__, __num_top__, __num_top__).a,
                        new boundBar(__num_top__, __num_top__, __num_top__, __num_top__, __num_top__, __num_top__, __num_top__).a
                    ];
                case __num_top__:
                    return [
                        new boundFoo(__num_top__, __num_top__, __num_top__, __num_top__, __num_top__, __num_top__, __num_top__, __num_top__).a,
                        new boundBar(__num_top__, __num_top__, __num_top__, __num_top__, __num_top__, __num_top__, __num_top__, __num_top__).a
                    ];
                }
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should ensure `new bound` is an instance of `func`', function (assert) {
        assert.expect(2);
        function Foo(value) {
            return value && object;
        }
        var bound = _.bind(Foo), object = {};
        assert.ok(new bound() instanceof Foo);
        assert.strictEqual(new bound(__bool_top__), object);
    });
    QUnit.test('should append array arguments to partially applied arguments', function (assert) {
        assert.expect(1);
        var object = {}, bound = _.bind(fn, object, __str_top__);
        assert.deepEqual(bound([__str_top__], __str_top__), [
            object,
            __str_top__,
            [__str_top__],
            __str_top__
        ]);
    });
    QUnit.test('should not rebind functions', function (assert) {
        assert.expect(3);
        var object1 = {}, object2 = {}, object3 = {};
        var bound1 = _.bind(fn, object1), bound2 = _.bind(bound1, object2, __str_top__), bound3 = _.bind(bound1, object3, __str_top__);
        assert.deepEqual(bound1(), [object1]);
        assert.deepEqual(bound2(), [
            object1,
            __str_top__
        ]);
        assert.deepEqual(bound3(), [
            object1,
            __str_top__
        ]);
    });
    QUnit.test('should not error when instantiating bound built-ins', function (assert) {
        assert.expect(2);
        var Ctor = _.bind(Date, null), expected = new Date(__num_top__, __num_top__, __num_top__, __num_top__, __num_top__, __num_top__, __num_top__);
        try {
            var actual = new Ctor(__num_top__, __num_top__, __num_top__, __num_top__, __num_top__, __num_top__, __num_top__);
        } catch (e) {
        }
        assert.deepEqual(actual, expected);
        Ctor = _.bind(Date, null, __num_top__, __num_top__, __num_top__);
        try {
            actual = new Ctor(__num_top__, __num_top__, __num_top__, __num_top__);
        } catch (e) {
        }
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should not error when calling bound class constructors with the `new` operator', function (assert) {
        assert.expect(1);
        var createCtor = lodashStable.attempt(Function, __str_top__);
        if (typeof createCtor == __str_top__) {
            var bound = _.bind(createCtor()), count = __num_top__, expected = lodashStable.times(count, stubTrue);
            var actual = lodashStable.times(count, function (index) {
                try {
                    switch (index) {
                    case __num_top__:
                        return !!new bound();
                    case __num_top__:
                        return !!new bound(__num_top__);
                    case __num_top__:
                        return !!new bound(__num_top__, __num_top__);
                    case __num_top__:
                        return !!new bound(__num_top__, __num_top__, __num_top__);
                    case __num_top__:
                        return !!new bound(__num_top__, __num_top__, __num_top__, __num_top__);
                    case __num_top__:
                        return !!new bound(__num_top__, __num_top__, __num_top__, __num_top__, __num_top__);
                    case __num_top__:
                        return !!new bound(__num_top__, __num_top__, __num_top__, __num_top__, __num_top__, __num_top__);
                    case __num_top__:
                        return !!new bound(__num_top__, __num_top__, __num_top__, __num_top__, __num_top__, __num_top__, __num_top__);
                    }
                } catch (e) {
                }
            });
            assert.deepEqual(actual, expected);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should return a wrapped value when chaining', function (assert) {
        assert.expect(2);
        if (!isNpm) {
            var object = {}, bound = _(fn).bind({}, __str_top__, __str_top__);
            assert.ok(bound instanceof _);
            var actual = bound.value()(__str_top__);
            assert.deepEqual(actual, [
                object,
                __str_top__,
                __str_top__,
                __str_top__
            ]);
        } else {
            skipAssert(assert, 2);
        }
    });
}());