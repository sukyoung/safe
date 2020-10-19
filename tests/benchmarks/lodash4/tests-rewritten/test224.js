QUnit.module('sortedIndex methods');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], isSortedIndex = methodName == __str_top__;
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var array = [
                __num_top__,
                __num_top__
            ], values = [
                __num_top__,
                __num_top__,
                __num_top__
            ], expected = isSortedIndex ? [
                __num_top__,
                __num_top__,
                __num_top__
            ] : [
                __num_top__,
                __num_top__,
                __num_top__
            ];
        var actual = lodashStable.map(values, function (value) {
            return func(array, value);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var array = [
                __str_top__,
                __str_top__
            ], values = [
                __str_top__,
                __str_top__,
                __str_top__
            ], expected = isSortedIndex ? [
                __num_top__,
                __num_top__,
                __num_top__
            ] : [
                __num_top__,
                __num_top__,
                __num_top__
            ];
        var actual = lodashStable.map(values, function (value) {
            return func(array, value);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var values = [
                null,
                undefined
            ], expected = lodashStable.map(values, lodashStable.constant([
                __num_top__,
                __num_top__,
                __num_top__
            ]));
        var actual = lodashStable.map(values, function (array) {
            return [
                func(array, __num_top__),
                func(array, undefined),
                func(array, NaN)
            ];
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(12);
        var symbol1 = Symbol ? Symbol(__str_top__) : null, symbol2 = Symbol ? Symbol(__str_top__) : null, symbol3 = Symbol ? Symbol(__str_top__) : null, expected = [
                __num_top__,
                __str_top__,
                {},
                symbol1,
                symbol2,
                null,
                undefined,
                NaN,
                NaN
            ];
        lodashStable.each([
            [
                NaN,
                symbol1,
                null,
                __num_top__,
                __str_top__,
                {},
                symbol2,
                NaN,
                undefined
            ],
            [
                __str_top__,
                null,
                __num_top__,
                symbol1,
                NaN,
                {},
                NaN,
                symbol2,
                undefined
            ]
        ], function (array) {
            assert.deepEqual(_.sortBy(array), expected);
            assert.strictEqual(func(expected, __num_top__), __num_top__);
            assert.strictEqual(func(expected, symbol3), isSortedIndex ? __num_top__ : Symbol ? __num_top__ : __num_top__);
            assert.strictEqual(func(expected, null), isSortedIndex ? Symbol ? __num_top__ : __num_top__ : __num_top__);
            assert.strictEqual(func(expected, undefined), isSortedIndex ? __num_top__ : __num_top__);
            assert.strictEqual(func(expected, NaN), isSortedIndex ? __num_top__ : __num_top__);
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(3);
        var array = [
            null,
            null
        ];
        assert.strictEqual(func(array, null), isSortedIndex ? __num_top__ : __num_top__);
        assert.strictEqual(func(array, __num_top__), __num_top__);
        assert.strictEqual(func(array, __str_top__), __num_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(3);
        var symbol1 = Symbol ? Symbol(__str_top__) : null, symbol2 = Symbol ? Symbol(__str_top__) : null, symbol3 = Symbol ? Symbol(__str_top__) : null, array = [
                symbol1,
                symbol2
            ];
        assert.strictEqual(func(array, symbol3), isSortedIndex ? __num_top__ : __num_top__);
        assert.strictEqual(func(array, __num_top__), __num_top__);
        assert.strictEqual(func(array, __str_top__), __num_top__);
    });
});