QUnit.module('lodash.subtract');
(function () {
    QUnit.test('should subtract two numbers', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.subtract(6, __num_top__), 2);
        assert.strictEqual(_.subtract(-__num_top__, 4), -__num_top__);
        assert.strictEqual(_.subtract(-6, -__num_top__), -2);
    });
    QUnit.test('should coerce arguments to numbers', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.subtract('6', '4'), 2);
        assert.deepEqual(_.subtract(__str_top__, 'y'), NaN);
    });
}());