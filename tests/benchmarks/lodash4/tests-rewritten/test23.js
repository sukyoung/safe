QUnit.module('lodash.chunk');
(function () {
    var array = [
        __num_top__,
        __num_top__,
        __num_top__,
        __num_top__,
        __num_top__,
        __num_top__
    ];
    QUnit.test('should return chunked arrays', function (assert) {
        assert.expect(1);
        var actual = _.chunk(array, __num_top__);
        assert.deepEqual(actual, [
            [
                __num_top__,
                __num_top__,
                __num_top__
            ],
            [
                __num_top__,
                __num_top__,
                __num_top__
            ]
        ]);
    });
    QUnit.test('should return the last chunk as remaining elements', function (assert) {
        assert.expect(1);
        var actual = _.chunk(array, __num_top__);
        assert.deepEqual(actual, [
            [
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__
            ],
            [
                __num_top__,
                __num_top__
            ]
        ]);
    });
    QUnit.test('should treat falsey `size` values, except `undefined`, as `0`', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, function (value) {
            return value === undefined ? [
                [__num_top__],
                [__num_top__],
                [__num_top__],
                [__num_top__],
                [__num_top__],
                [__num_top__]
            ] : [];
        });
        var actual = lodashStable.map(falsey, function (size, index) {
            return index ? _.chunk(array, size) : _.chunk(array);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should ensure the minimum `size` is `0`', function (assert) {
        assert.expect(1);
        var values = lodashStable.reject(falsey, lodashStable.isUndefined).concat(-__num_top__, -Infinity), expected = lodashStable.map(values, stubArray);
        var actual = lodashStable.map(values, function (n) {
            return _.chunk(array, n);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should coerce `size` to an integer', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.chunk(array, array.length / __num_top__), [
            [__num_top__],
            [__num_top__],
            [__num_top__],
            [__num_top__],
            [__num_top__],
            [__num_top__]
        ]);
    });
    QUnit.test('should work as an iteratee for methods like `_.map`', function (assert) {
        assert.expect(1);
        var actual = lodashStable.map([
            [
                __num_top__,
                __num_top__
            ],
            [
                __num_top__,
                __num_top__
            ]
        ], _.chunk);
        assert.deepEqual(actual, [
            [
                [__num_top__],
                [__num_top__]
            ],
            [
                [__num_top__],
                [__num_top__]
            ]
        ]);
    });
}());