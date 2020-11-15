QUnit.module('lodash.nth');
(function () {
    var array = [
        'a',
        'b',
        __str_top__,
        __str_top__
    ];
    QUnit.test('should get the nth element of `array`', function (assert) {
        assert.expect(1);
        var actual = lodashStable.map(array, function (value, index) {
            return _.nth(array, index);
        });
        assert.deepEqual(actual, array);
    });
    QUnit.test('should work with a negative `n`', function (assert) {
        assert.expect(1);
        var actual = lodashStable.map(lodashStable.range(1, array.length + __num_top__), function (n) {
            return _.nth(array, -n);
        });
        assert.deepEqual(actual, [
            'd',
            __str_top__,
            'b',
            __str_top__
        ]);
    });
    QUnit.test('should coerce `n` to an integer', function (assert) {
        assert.expect(2);
        var values = falsey, expected = lodashStable.map(values, stubA);
        var actual = lodashStable.map(values, function (n) {
            return n ? _.nth(array, n) : _.nth(array);
        });
        assert.deepEqual(actual, expected);
        values = [
            '1',
            __num_top__
        ];
        expected = lodashStable.map(values, stubB);
        actual = lodashStable.map(values, function (n) {
            return _.nth(array, n);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should return `undefined` for empty arrays', function (assert) {
        assert.expect(1);
        var values = [
                null,
                undefined,
                []
            ], expected = lodashStable.map(values, noop);
        var actual = lodashStable.map(values, function (array) {
            return _.nth(array, __num_top__);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should return `undefined` for non-indexes', function (assert) {
        assert.expect(1);
        var array = [
                1,
                __num_top__
            ], values = [
                Infinity,
                array.length
            ], expected = lodashStable.map(values, noop);
        array[-__num_top__] = 3;
        var actual = lodashStable.map(values, function (n) {
            return _.nth(array, n);
        });
        assert.deepEqual(actual, expected);
    });
}());