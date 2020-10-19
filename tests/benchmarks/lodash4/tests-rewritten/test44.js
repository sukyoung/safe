QUnit.module('difference methods');
lodashStable.each([
    __str_top__,
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName];
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = func([
            __num_top__,
            __num_top__
        ], [
            __num_top__,
            __num_top__
        ]);
        assert.deepEqual(actual, [__num_top__]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = func([
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ], [
            __num_top__,
            __num_top__
        ], [
            __num_top__,
            __num_top__
        ]);
        assert.deepEqual(actual, [__num_top__]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var array = [
            -__num_top__,
            __num_top__
        ];
        var actual = lodashStable.map(array, function (value) {
            return func(array, [value]);
        });
        assert.deepEqual(actual, [
            [],
            []
        ]);
        actual = lodashStable.map(func([
            -__num_top__,
            __num_top__
        ], [__num_top__]), lodashStable.toString);
        assert.deepEqual(actual, [__str_top__]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        assert.deepEqual(func([
            __num_top__,
            NaN,
            __num_top__
        ], [
            NaN,
            __num_top__,
            NaN
        ]), [
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var array1 = lodashStable.range(LARGE_ARRAY_SIZE + __num_top__), array2 = lodashStable.range(LARGE_ARRAY_SIZE), a = {}, b = {}, c = {};
        array1.push(a, b, c);
        array2.push(b, c, a);
        assert.deepEqual(func(array1, array2), [LARGE_ARRAY_SIZE]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var array = [
            -__num_top__,
            __num_top__
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
            -__num_top__,
            __num_top__
        ], largeArray), lodashStable.toString);
        assert.deepEqual(actual, [__str_top__]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var largeArray = lodashStable.times(LARGE_ARRAY_SIZE, stubNaN);
        assert.deepEqual(func([
            __num_top__,
            NaN,
            __num_top__
        ], largeArray), [
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var object1 = {}, object2 = {}, largeArray = lodashStable.times(LARGE_ARRAY_SIZE, lodashStable.constant(object1));
        assert.deepEqual(func([
            object1,
            object2
        ], largeArray), [object2]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(3);
        var array = [
            __num_top__,
            null,
            __num_top__
        ];
        assert.deepEqual(func(args, __num_top__, { '0': __num_top__ }), [
            __num_top__,
            __num_top__,
            __num_top__
        ]);
        assert.deepEqual(func(null, array, __num_top__), []);
        assert.deepEqual(func(array, args, null), [null]);
    });
});