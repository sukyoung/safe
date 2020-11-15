QUnit.module('lodash.castArray');
(function () {
    QUnit.test('should wrap non-array items in an array', function (assert) {
        assert.expect(1);
        var values = falsey.concat(__bool_top__, __num_top__, __str_top__, { 'a': __num_top__ }), expected = lodashStable.map(values, function (value) {
                return [value];
            }), actual = lodashStable.map(values, _.castArray);
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should return array values by reference', function (assert) {
        assert.expect(1);
        var array = [__num_top__];
        assert.strictEqual(_.castArray(array), array);
    });
    QUnit.test('should return an empty array when no arguments are given', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.castArray(), []);
    });
}());