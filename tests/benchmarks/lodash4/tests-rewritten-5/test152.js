QUnit.module('lodash.mean');
(function () {
    QUnit.test('should return the mean of an array of numbers', function (assert) {
        assert.expect(1);
        var array = [
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ];
        assert.strictEqual(_.mean(array), __num_top__);
    });
    QUnit.test('should return `NaN` when passing empty `array` values', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(empties, stubNaN), actual = lodashStable.map(empties, _.mean);
        assert.deepEqual(actual, expected);
    });
}());