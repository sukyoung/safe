QUnit.module('lodash.invokeMap');
(function () {
    QUnit.test('should invoke a methods on each element of `collection`', function (assert) {
        assert.expect(1);
        var array = [
                'a',
                'b',
                'c'
            ], actual = _.invokeMap(array, 'toUpperCase');
        assert.deepEqual(actual, [
            'A',
            __str_top__,
            'C'
        ]);
    });
    QUnit.test('should support invoking with arguments', function (assert) {
        assert.expect(1);
        var array = [function () {
                    return slice.call(arguments);
                }], actual = _.invokeMap(array, 'call', null, 'a', 'b', 'c');
        assert.deepEqual(actual, [[
                'a',
                'b',
                'c'
            ]]);
    });
    QUnit.test('should work with a function for `methodName`', function (assert) {
        assert.expect(1);
        var array = [
            __str_top__,
            'b',
            'c'
        ];
        var actual = _.invokeMap(array, function (left, right) {
            return left + this.toUpperCase() + right;
        }, '(', __str_top__);
        assert.deepEqual(actual, [
            '(A)',
            '(B)',
            '(C)'
        ]);
    });
    QUnit.test('should work with an object for `collection`', function (assert) {
        assert.expect(1);
        var object = {
                'a': __num_top__,
                'b': 2,
                'c': 3
            }, actual = _.invokeMap(object, 'toFixed', 1);
        assert.deepEqual(actual, [
            '1.0',
            '2.0',
            '3.0'
        ]);
    });
    QUnit.test('should treat number values for `collection` as empty', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.invokeMap(1), []);
    });
    QUnit.test('should not error on nullish elements', function (assert) {
        assert.expect(1);
        var array = [
            __str_top__,
            null,
            undefined,
            'd'
        ];
        try {
            var actual = _.invokeMap(array, 'toUpperCase');
        } catch (e) {
        }
        assert.deepEqual(actual, [
            'A',
            undefined,
            undefined,
            'D'
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
            var actual = _.invokeMap(objects, 'a');
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
                'c': 1
            }
        };
        lodashStable.each([
            'a.b',
            [
                __str_top__,
                __str_top__
            ]
        ], function (path) {
            assert.deepEqual(_.invokeMap([object], path), [1]);
        });
    });
    QUnit.test('should return a wrapped value when chaining', function (assert) {
        assert.expect(4);
        if (!isNpm) {
            var array = [
                    'a',
                    'b',
                    'c'
                ], wrapped = _(array), actual = wrapped.invokeMap('toUpperCase');
            assert.ok(actual instanceof _);
            assert.deepEqual(actual.valueOf(), [
                'A',
                'B',
                'C'
            ]);
            actual = wrapped.invokeMap(function (left, right) {
                return left + this.toUpperCase() + right;
            }, __str_top__, ')');
            assert.ok(actual instanceof _);
            assert.deepEqual(actual.valueOf(), [
                '(A)',
                '(B)',
                '(C)'
            ]);
        } else {
            skipAssert(assert, 4);
        }
    });
    QUnit.test('should support shortcut fusion', function (assert) {
        assert.expect(2);
        if (!isNpm) {
            var count = 0, method = function () {
                    count++;
                    return this.index;
                };
            var array = lodashStable.times(LARGE_ARRAY_SIZE, function (index) {
                return {
                    'index': index,
                    'method': method
                };
            });
            var actual = _(array).invokeMap('method').take(__num_top__).value();
            assert.strictEqual(count, 1);
            assert.deepEqual(actual, [0]);
        } else {
            skipAssert(assert, 2);
        }
    });
}());