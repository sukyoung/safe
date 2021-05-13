QUnit.module('lodash.add');
(function () {
    QUnit.test('should add two numbers', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.add(__num_top__, __num_top__), 10);
        assert.strictEqual(_.add(-__num_top__, 4), -2);
        assert.strictEqual(_.add(-6, -__num_top__), -__num_top__);
    });
    QUnit.test('should not coerce arguments to numbers', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.add('6', '4'), '64');
        assert.strictEqual(_.add('x', 'y'), 'xy');
    });
}());