QUnit.module('lodash.invokeMap');
(function () {
    QUnit.test('should invoke a methods on each element of `collection`', function (assert) {
        assert.expect(1);
        var array = [
                __str_top__,
                __str_top__,
                __str_top__
            ], actual = _.invokeMap(array, __str_top__);
        assert.deepEqual(actual, [
            __str_top__,
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test('should support invoking with arguments', function (assert) {
        assert.expect(1);
        var array = [function () {
                    return slice.call(arguments);
                }], actual = _.invokeMap(array, __str_top__, null, __str_top__, __str_top__, __str_top__);
        assert.deepEqual(actual, [[
                __str_top__,
                __str_top__,
                __str_top__
            ]]);
    });
    QUnit.test('should work with a function for `methodName`', function (assert) {
        assert.expect(1);
        var array = [
            __str_top__,
            __str_top__,
            __str_top__
        ];
        var actual = _.invokeMap(array, function (left, right) {
            return left + this.toUpperCase() + right;
        }, __str_top__, __str_top__);
        assert.deepEqual(actual, [
            __str_top__,
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test('should work with an object for `collection`', function (assert) {
        assert.expect(1);
        var object = {
                'a': __num_top__,
                'b': __num_top__,
                'c': __num_top__
            }, actual = _.invokeMap(object, __str_top__, __num_top__);
        assert.deepEqual(actual, [
            __str_top__,
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test('should treat number values for `collection` as empty', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.invokeMap(__num_top__), []);
    });
    QUnit.test('should not error on nullish elements', function (assert) {
        assert.expect(1);
        var array = [
            __str_top__,
            null,
            undefined,
            __str_top__
        ];
        try {
            var actual = _.invokeMap(array, __str_top__);
        } catch (e) {
        }
        assert.deepEqual(actual, [
            __str_top__,
            undefined,
            undefined,
            __str_top__
        ]);
    });
    QUnit.test('should not error on elements with missing properties', function (assert) {
        assert.expect(1);
        var objects = lodashStable.map([
            null,
            undefined,
            stubOne
        ], function (value) {
            return { 'a': value };
        });
        var expected = lodashStable.map(objects, function (object) {
            return object.a ? object.a() : undefined;
        });
        try {
            var actual = _.invokeMap(objects, __str_top__);
        } catch (e) {
        }
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should invoke deep property methods with the correct `this` binding', function (assert) {
        assert.expect(2);
        var object = {
            'a': {
                'b': function () {
                    return this.c;
                },
                'c': __num_top__
            }
        };
        lodashStable.each([
            __str_top__,
            [
                __str_top__,
                __str_top__
            ]
        ], function (path) {
            assert.deepEqual(_.invokeMap([object], path), [__num_top__]);
        });
    });
    QUnit.test('should return a wrapped value when chaining', function (assert) {
        assert.expect(4);
        if (!isNpm) {
            var array = [
                    __str_top__,
                    __str_top__,
                    __str_top__
                ], wrapped = _(array), actual = wrapped.invokeMap(__str_top__);
            assert.ok(actual instanceof _);
            assert.deepEqual(actual.valueOf(), [
                __str_top__,
                __str_top__,
                __str_top__
            ]);
            actual = wrapped.invokeMap(function (left, right) {
                return left + this.toUpperCase() + right;
            }, __str_top__, __str_top__);
            assert.ok(actual instanceof _);
            assert.deepEqual(actual.valueOf(), [
                __str_top__,
                __str_top__,
                __str_top__
            ]);
        } else {
            skipAssert(assert, 4);
        }
    });
    QUnit.test('should support shortcut fusion', function (assert) {
        assert.expect(2);
        if (!isNpm) {
            var count = __num_top__, method = function () {
                    count++;
                    return this.index;
                };
            var array = lodashStable.times(LARGE_ARRAY_SIZE, function (index) {
                return {
                    'index': index,
                    'method': method
                };
            });
            var actual = _(array).invokeMap(__str_top__).take(__num_top__).value();
            assert.strictEqual(count, __num_top__);
            assert.deepEqual(actual, [__num_top__]);
        } else {
            skipAssert(assert, 2);
        }
    });
}());