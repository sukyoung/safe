QUnit.module('lodash.multiply');
(function () {
    QUnit.test('should multiply two numbers', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.multiply(6, 4), 24);
        assert.strictEqual(_.multiply(-6, 4), -24);
        assert.strictEqual(_.multiply(-6, -4), 24);
    });
    QUnit.test('should coerce arguments to numbers', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.multiply('6', __str_top__), 24);
        assert.deepEqual(_.multiply('x', 'y'), NaN);
    });
}());