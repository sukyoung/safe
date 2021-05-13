QUnit.module('lodash.defaultTo');
(function () {
    QUnit.test('should return a default value if `value` is `NaN` or nullish', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, function (value) {
            return value == null || value !== value ? __num_top__ : value;
        });
        var actual = lodashStable.map(falsey, function (value) {
            return _.defaultTo(value, __num_top__);
        });
        assert.deepEqual(actual, expected);
    });
}());