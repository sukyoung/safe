QUnit.module('lodash.divide');
(function () {
    QUnit.test('should divide two numbers', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.divide(__num_top__, __num_top__), 1.5);
        assert.strictEqual(_.divide(-__num_top__, 4), -__num_top__);
        assert.strictEqual(_.divide(-6, -__num_top__), __num_top__);
    });
    QUnit.test('should coerce arguments to numbers', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.divide(__str_top__, '4'), __num_top__);
        assert.deepEqual(_.divide(__str_top__, __str_top__), NaN);
    });
}());