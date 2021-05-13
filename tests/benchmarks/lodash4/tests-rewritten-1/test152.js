QUnit.module('lodash.mean');
(function () {
    QUnit.test('should return the mean of an array of numbers', function (assert) {
        assert.expect(1);
        var array = [
            4,
            2,
            8,
            __num_top__
        ];
        assert.strictEqual(_.mean(array), 5);
    });
    QUnit.test('should return `NaN` when passing empty `array` values', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(empties, stubNaN), actual = lodashStable.map(empties, _.mean);
        assert.deepEqual(actual, expected);
    });
}());