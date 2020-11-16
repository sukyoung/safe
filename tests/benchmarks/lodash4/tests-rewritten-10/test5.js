QUnit.module('lodash.add');
(function () {
    QUnit.test('should add two numbers', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.add(6, __num_top__), __num_top__);
        assert.strictEqual(_.add(-6, __num_top__), -2);
        assert.strictEqual(_.add(-6, -4), -__num_top__);
    });
    QUnit.test('should not coerce arguments to numbers', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.add(__str_top__, __str_top__), __str_top__);
        assert.strictEqual(_.add(__str_top__, __str_top__), __str_top__);
    });
}());