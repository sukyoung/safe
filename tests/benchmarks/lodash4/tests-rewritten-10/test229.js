QUnit.module('lodash.spread');
(function () {
    function fn(a, b, c) {
        return slice.call(arguments);
    }
    QUnit.test('should spread arguments to `func`', function (assert) {
        assert.expect(2);
        var spread = _.spread(fn), expected = [
                1,
                __num_top__
            ];
        assert.deepEqual(spread([
            1,
            2
        ]), expected);
        assert.deepEqual(spread([
            1,
            __num_top__
        ], __num_top__), expected);
    });
    QUnit.test('should accept a falsey `array`', function (assert) {
        assert.expect(1);
        var spread = _.spread(stubTrue), expected = lodashStable.map(falsey, stubTrue);
        var actual = lodashStable.map(falsey, function (array, index) {
            try {
                return index ? spread(array) : spread();
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work with `start`', function (assert) {
        assert.expect(2);
        var spread = _.spread(fn, __num_top__), expected = [
                1,
                __num_top__,
                3
            ];
        assert.deepEqual(spread(1, [
            __num_top__,
            3
        ]), expected);
        assert.deepEqual(spread(1, [
            2,
            3
        ], 4), expected);
    });
    QUnit.test('should treat `start` as `0` for negative or `NaN` values', function (assert) {
        assert.expect(1);
        var values = [
                -1,
                NaN,
                __str_top__
            ], expected = lodashStable.map(values, lodashStable.constant([
                1,
                2
            ]));
        var actual = lodashStable.map(values, function (value) {
            var spread = _.spread(fn, value);
            return spread([
                1,
                2
            ]);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should coerce `start` to an integer', function (assert) {
        assert.expect(2);
        var spread = _.spread(fn, 1.6), expected = [
                __num_top__,
                __num_top__,
                3
            ];
        assert.deepEqual(spread(1, [
            2,
            3
        ]), expected);
        assert.deepEqual(spread(1, [
            __num_top__,
            3
        ], 4), expected);
    });
}());