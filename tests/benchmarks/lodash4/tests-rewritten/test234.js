QUnit.module('lodash.subtract');
(function () {
    QUnit.test('should subtract two numbers', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.subtract(__num_top__, __num_top__), __num_top__);
        assert.strictEqual(_.subtract(-__num_top__, __num_top__), -__num_top__);
        assert.strictEqual(_.subtract(-__num_top__, -__num_top__), -__num_top__);
    });
    QUnit.test('should coerce arguments to numbers', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.subtract(__str_top__, __str_top__), __num_top__);
        assert.deepEqual(_.subtract(__str_top__, __str_top__), NaN);
    });
}());