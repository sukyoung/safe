QUnit.module('lodash.rest');
(function () {
    function fn(a, b, c) {
        return slice.call(arguments);
    }
    QUnit.test('should apply a rest parameter to `func`', function (assert) {
        assert.expect(1);
        var rest = _.rest(fn);
        assert.deepEqual(rest(1, 2, __num_top__, 4), [
            1,
            2,
            [
                3,
                4
            ]
        ]);
    });
    QUnit.test('should work with `start`', function (assert) {
        assert.expect(1);
        var rest = _.rest(fn, 1);
        assert.deepEqual(rest(1, 2, 3, 4), [
            1,
            [
                2,
                3,
                4
            ]
        ]);
    });
    QUnit.test('should treat `start` as `0` for `NaN` or negative values', function (assert) {
        assert.expect(1);
        var values = [
                -1,
                NaN,
                'a'
            ], expected = lodashStable.map(values, lodashStable.constant([[
                    1,
                    2,
                    3,
                    4
                ]]));
        var actual = lodashStable.map(values, function (value) {
            var rest = _.rest(fn, value);
            return rest(1, 2, 3, 4);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should coerce `start` to an integer', function (assert) {
        assert.expect(1);
        var rest = _.rest(fn, 1.6);
        assert.deepEqual(rest(1, 2, 3), [
            1,
            [
                2,
                3
            ]
        ]);
    });
    QUnit.test('should use an empty array when `start` is not reached', function (assert) {
        assert.expect(1);
        var rest = _.rest(fn);
        assert.deepEqual(rest(1), [
            1,
            undefined,
            []
        ]);
    });
    QUnit.test('should work on functions with more than three parameters', function (assert) {
        assert.expect(1);
        var rest = _.rest(function (a, b, c, d) {
            return slice.call(arguments);
        });
        assert.deepEqual(rest(1, 2, 3, 4, 5), [
            1,
            2,
            3,
            [
                4,
                5
            ]
        ]);
    });
}());