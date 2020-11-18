QUnit.module('difference methods');
lodashStable.each([
    'difference',
    'differenceBy',
    'differenceWith'
], function (methodName) {
    var func = _[methodName];
    QUnit.test('`_.' + methodName + '` should return the difference of two arrays', function (assert) {
        assert.expect(1);
        var actual = func([
            2,
            1
        ], [
            2,
            3
        ]);
        assert.deepEqual(actual, [1]);
    });
    QUnit.test('`_.' + methodName + '` should return the difference of multiple arrays', function (assert) {
        assert.expect(1);
        var actual = func([
            2,
            1,
            2,
            3
        ], [
            3,
            4
        ], [
            3,
            2
        ]);
        assert.deepEqual(actual, [1]);
    });
    QUnit.test('`_.' + methodName + '` should treat `-0` as `0`', function (assert) {
        assert.expect(2);
        var array = [
            -0,
            0
        ];
        var actual = lodashStable.map(array, function (value) {
            return func(array, [value]);
        });
        assert.deepEqual(actual, [
            [],
            []
        ]);
        actual = lodashStable.map(func([
            -0,
            1
        ], [1]), lodashStable.toString);
        assert.deepEqual(actual, ['0']);
    });
    QUnit.test(__str_top__ + methodName + '` should match `NaN`', function (assert) {
        assert.expect(1);
        assert.deepEqual(func([
            1,
            NaN,
            3
        ], [
            NaN,
            5,
            NaN
        ]), [
            1,
            3
        ]);
    });
    QUnit.test('`_.' + methodName + '` should work with large arrays', function (assert) {
        assert.expect(1);
        var array1 = lodashStable.range(LARGE_ARRAY_SIZE + 1), array2 = lodashStable.range(LARGE_ARRAY_SIZE), a = {}, b = {}, c = {};
        array1.push(a, b, c);
        array2.push(b, c, a);
        assert.deepEqual(func(array1, array2), [LARGE_ARRAY_SIZE]);
    });
    QUnit.test('`_.' + methodName + '` should work with large arrays of `-0` as `0`', function (assert) {
        assert.expect(2);
        var array = [
            -0,
            0
        ];
        var actual = lodashStable.map(array, function (value) {
            var largeArray = lodashStable.times(LARGE_ARRAY_SIZE, lodashStable.constant(value));
            return func(array, largeArray);
        });
        assert.deepEqual(actual, [
            [],
            []
        ]);
        var largeArray = lodashStable.times(LARGE_ARRAY_SIZE, stubOne);
        actual = lodashStable.map(func([
            -0,
            1
        ], largeArray), lodashStable.toString);
        assert.deepEqual(actual, ['0']);
    });
    QUnit.test('`_.' + methodName + '` should work with large arrays of `NaN`', function (assert) {
        assert.expect(1);
        var largeArray = lodashStable.times(LARGE_ARRAY_SIZE, stubNaN);
        assert.deepEqual(func([
            1,
            NaN,
            3
        ], largeArray), [
            1,
            3
        ]);
    });
    QUnit.test('`_.' + methodName + '` should work with large arrays of objects', function (assert) {
        assert.expect(1);
        var object1 = {}, object2 = {}, largeArray = lodashStable.times(LARGE_ARRAY_SIZE, lodashStable.constant(object1));
        assert.deepEqual(func([
            object1,
            object2
        ], largeArray), [object2]);
    });
    QUnit.test('`_.' + methodName + '` should ignore values that are not array-like', function (assert) {
        assert.expect(3);
        var array = [
            1,
            null,
            3
        ];
        assert.deepEqual(func(args, 3, { '0': 1 }), [
            1,
            2,
            3
        ]);
        assert.deepEqual(func(null, array, 1), []);
        assert.deepEqual(func(array, args, null), [null]);
    });
});