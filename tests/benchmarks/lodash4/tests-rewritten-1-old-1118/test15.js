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
        assert.deepEqual(bound('a'), [
            object,
            'a'
        ]);
    });
    QUnit.test('should accept a falsey `thisArg`', function (assert) {
        assert.expect(1);
        var values = lodashStable.reject(falsey.slice(1), function (value) {
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
        var bound = _.bind(fn, null), actual = bound('a');
        assert.ok(actual[0] === null || actual[0] && actual[0].Array);
        assert.strictEqual(actual[1], 'a');
        lodashStable.times(2, function (index) {
            bound = index ? _.bind(fn, undefined) : _.bind(fn);
            actual = bound('b');
            assert.ok(actual[0] === undefined || actual[0] && actual[0].Array);
            assert.strictEqual(actual[1], 'b');
        });
    });
    QUnit.test('should partially apply arguments ', function (assert) {
        assert.expect(4);
        var object = {}, bound = _.bind(fn, object, 'a');
        assert.deepEqual(bound(), [
            object,
            'a'
        ]);
        bound = _.bind(fn, object, 'a');
        assert.deepEqual(bound('b'), [
            object,
            'a',
            'b'
        ]);
        bound = _.bind(fn, object, 'a', 'b');
        assert.deepEqual(bound(), [
            object,
            'a',
            'b'
        ]);
        assert.deepEqual(bound('c', 'd'), [
            object,
            'a',
            'b',
            'c',
            'd'
        ]);
    });
    QUnit.test('should support placeholders', function (assert) {
        assert.expect(4);
        var object = {}, ph = _.bind.placeholder, bound = _.bind(fn, object, ph, 'b', ph);
        assert.deepEqual(bound('a', 'c'), [
            object,
            'a',
            'b',
            'c'
        ]);
        assert.deepEqual(bound('a'), [
            object,
            'a',
            'b',
            undefined
        ]);
        assert.deepEqual(bound('a', 'c', 'd'), [
            object,
            'a',
            'b',
            'c',
            'd'
        ]);
        assert.deepEqual(bound(), [
            object,
            undefined,
            'b',
            undefined
        ]);
    });
    QUnit.test('should use `_.placeholder` when set', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            var _ph = _.placeholder = {}, ph = _.bind.placeholder, object = {}, bound = _.bind(fn, object, _ph, 'b', ph);
            assert.deepEqual(bound('a', 'c'), [
                object,
                'a',
                'b',
                ph,
                'c'
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
        assert.strictEqual(bound.length, 0);
        bound = _.bind(fn, {}, 1);
        assert.strictEqual(bound.length, 0);
    });
    QUnit.test('should ignore binding when called with the `new` operator', function (assert) {
        assert.expect(3);
        function Foo() {
            return this;
        }
        var bound = _.bind(Foo, { 'a': 1 }), newBound = new bound();
        assert.strictEqual(bound().a, 1);
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
        var thisArg = { 'a': 1 }, boundFoo = _.bind(Foo, thisArg), boundBar = _.bind(Bar, thisArg), count = 9, expected = lodashStable.times(count, lodashStable.constant([
                undefined,
                undefined
            ]));
        var actual = lodashStable.times(count, function (index) {
            try {
                switch (index) {
                case 0:
                    return [
                        new boundFoo().a,
                        new boundBar().a
                    ];
                case 1:
                    return [
                        new boundFoo(1).a,
                        new boundBar(1).a
                    ];
                case 2:
                    return [
                        new boundFoo(1, 2).a,
                        new boundBar(1, 2).a
                    ];
                case 3:
                    return [
                        new boundFoo(1, 2, 3).a,
                        new boundBar(1, 2, 3).a
                    ];
                case 4:
                    return [
                        new boundFoo(1, 2, 3, 4).a,
                        new boundBar(1, 2, 3, 4).a
                    ];
                case 5:
                    return [
                        new boundFoo(1, 2, 3, 4, 5).a,
                        new boundBar(1, 2, 3, 4, 5).a
                    ];
                case 6:
                    return [
                        new boundFoo(1, 2, 3, 4, 5, 6).a,
                        new boundBar(1, 2, 3, 4, 5, 6).a
                    ];
                case 7:
                    return [
                        new boundFoo(1, 2, 3, 4, 5, 6, 7).a,
                        new boundBar(1, 2, 3, 4, 5, 6, 7).a
                    ];
                case 8:
                    return [
                        new boundFoo(1, 2, 3, 4, 5, 6, 7, 8).a,
                        new boundBar(1, 2, 3, 4, 5, 6, 7, 8).a
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
        assert.strictEqual(new bound(true), object);
    });
    QUnit.test('should append array arguments to partially applied arguments', function (assert) {
        assert.expect(1);
        var object = {}, bound = _.bind(fn, object, 'a');
        assert.deepEqual(bound(['b'], 'c'), [
            object,
            'a',
            ['b'],
            'c'
        ]);
    });
    QUnit.test('should not rebind functions', function (assert) {
        assert.expect(3);
        var object1 = {}, object2 = {}, object3 = {};
        var bound1 = _.bind(fn, object1), bound2 = _.bind(bound1, object2, 'a'), bound3 = _.bind(bound1, object3, 'b');
        assert.deepEqual(bound1(), [object1]);
        assert.deepEqual(bound2(), [
            object1,
            'a'
        ]);
        assert.deepEqual(bound3(), [
            object1,
            __str_top__
        ]);
    });
    QUnit.test('should not error when instantiating bound built-ins', function (assert) {
        assert.expect(2);
        var Ctor = _.bind(Date, null), expected = new Date(2012, 4, 23, 0, 0, 0, 0);
        try {
            var actual = new Ctor(2012, 4, 23, 0, 0, 0, 0);
        } catch (e) {
        }
        assert.deepEqual(actual, expected);
        Ctor = _.bind(Date, null, 2012, 4, 23);
        try {
            actual = new Ctor(0, 0, 0, 0);
        } catch (e) {
        }
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should not error when calling bound class constructors with the `new` operator', function (assert) {
        assert.expect(1);
        var createCtor = lodashStable.attempt(Function, '"use strict";return class A{}');
        if (typeof createCtor == 'function') {
            var bound = _.bind(createCtor()), count = 8, expected = lodashStable.times(count, stubTrue);
            var actual = lodashStable.times(count, function (index) {
                try {
                    switch (index) {
                    case 0:
                        return !!new bound();
                    case 1:
                        return !!new bound(1);
                    case 2:
                        return !!new bound(1, 2);
                    case 3:
                        return !!new bound(1, 2, 3);
                    case 4:
                        return !!new bound(1, 2, 3, 4);
                    case 5:
                        return !!new bound(1, 2, 3, 4, 5);
                    case 6:
                        return !!new bound(1, 2, 3, 4, 5, 6);
                    case 7:
                        return !!new bound(1, 2, 3, 4, 5, 6, 7);
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
            var object = {}, bound = _(fn).bind({}, 'a', 'b');
            assert.ok(bound instanceof _);
            var actual = bound.value()('c');
            assert.deepEqual(actual, [
                object,
                'a',
                'b',
                'c'
            ]);
        } else {
            skipAssert(assert, 2);
        }
    });
}());