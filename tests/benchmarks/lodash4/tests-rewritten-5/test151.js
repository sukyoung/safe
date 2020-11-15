QUnit.module('lodash.max');
(function () {
    QUnit.test('should return the largest value from a collection', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.max([
            __num_top__,
            2,
            __num_top__
        ]), 3);
    });
    QUnit.test('should return `undefined` for empty collections', function (assert) {
        assert.expect(1);
        var values = falsey.concat([[]]), expected = lodashStable.map(values, noop);
        var actual = lodashStable.map(values, function (value, index) {
            try {
                return index ? _.max(value) : _.max();
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work with non-numeric collection values', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.max([
            __str_top__,
            __str_top__
        ]), __str_top__);
    });
}());