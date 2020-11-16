QUnit.module('lodash.multiply');
(function () {
    QUnit.test('should multiply two numbers', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.multiply(6, 4), 24);
        assert.strictEqual(_.multiply(-__num_top__, __num_top__), -24);
        assert.strictEqual(_.multiply(-6, -__num_top__), 24);
    });
    QUnit.test('should coerce arguments to numbers', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.multiply('6', '4'), 24);
        assert.deepEqual(_.multiply(__str_top__, __str_top__), NaN);
    });
}());