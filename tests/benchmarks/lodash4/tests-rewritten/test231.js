QUnit.module('lodash.startsWith');
(function () {
    var string = __str_top__;
    QUnit.test('should return `true` if a string starts with `target`', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.startsWith(string, __str_top__), __bool_top__);
    });
    QUnit.test('should return `false` if a string does not start with `target`', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.startsWith(string, __str_top__), __bool_top__);
    });
    QUnit.test('should work with a `position`', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.startsWith(string, __str_top__, __num_top__), __bool_top__);
    });
    QUnit.test('should work with `position` >= `length`', function (assert) {
        assert.expect(4);
        lodashStable.each([
            __num_top__,
            __num_top__,
            MAX_SAFE_INTEGER,
            Infinity
        ], function (position) {
            assert.strictEqual(_.startsWith(string, __str_top__, position), __bool_top__);
        });
    });
    QUnit.test('should treat falsey `position` values as `0`', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, stubTrue);
        var actual = lodashStable.map(falsey, function (position) {
            return _.startsWith(string, __str_top__, position);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should treat a negative `position` as `0`', function (assert) {
        assert.expect(6);
        lodashStable.each([
            -__num_top__,
            -__num_top__,
            -Infinity
        ], function (position) {
            assert.strictEqual(_.startsWith(string, __str_top__, position), __bool_top__);
            assert.strictEqual(_.startsWith(string, __str_top__, position), __bool_top__);
        });
    });
    QUnit.test('should coerce `position` to an integer', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.startsWith(string, __str_top__, __num_top__), __bool_top__);
    });
}());