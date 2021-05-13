QUnit.module('lodash.rest');
(function () {
    function fn(a, b, c) {
        return slice.call(arguments);
    }
    QUnit.test('should apply a rest parameter to `func`', function (assert) {
        assert.expect(1);
        var rest = _.rest(fn);
        assert.deepEqual(rest(__num_top__, __num_top__, __num_top__, __num_top__), [
            __num_top__,
            __num_top__,
            [
                __num_top__,
                __num_top__
            ]
        ]);
    });
    QUnit.test('should work with `start`', function (assert) {
        assert.expect(1);
        var rest = _.rest(fn, __num_top__);
        assert.deepEqual(rest(__num_top__, __num_top__, __num_top__, __num_top__), [
            __num_top__,
            [
                __num_top__,
                __num_top__,
                __num_top__
            ]
        ]);
    });
    QUnit.test('should treat `start` as `0` for `NaN` or negative values', function (assert) {
        assert.expect(1);
        var values = [
                -__num_top__,
                NaN,
                __str_top__
            ], expected = lodashStable.map(values, lodashStable.constant([[
                    __num_top__,
                    __num_top__,
                    __num_top__,
                    __num_top__
                ]]));
        var actual = lodashStable.map(values, function (value) {
            var rest = _.rest(fn, value);
            return rest(__num_top__, __num_top__, __num_top__, __num_top__);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should coerce `start` to an integer', function (assert) {
        assert.expect(1);
        var rest = _.rest(fn, __num_top__);
        assert.deepEqual(rest(__num_top__, __num_top__, __num_top__), [
            __num_top__,
            [
                __num_top__,
                __num_top__
            ]
        ]);
    });
    QUnit.test('should use an empty array when `start` is not reached', function (assert) {
        assert.expect(1);
        var rest = _.rest(fn);
        assert.deepEqual(rest(__num_top__), [
            __num_top__,
            undefined,
            []
        ]);
    });
    QUnit.test('should work on functions with more than three parameters', function (assert) {
        assert.expect(1);
        var rest = _.rest(function (a, b, c, d) {
            return slice.call(arguments);
        });
        assert.deepEqual(rest(__num_top__, __num_top__, __num_top__, __num_top__, __num_top__), [
            __num_top__,
            __num_top__,
            __num_top__,
            [
                __num_top__,
                __num_top__
            ]
        ]);
    });
}());