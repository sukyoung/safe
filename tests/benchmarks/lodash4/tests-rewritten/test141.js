QUnit.module('lodash.lte');
(function () {
    QUnit.test('should return `true` if `value` is <= `other`', function (assert) {
        assert.expect(4);
        assert.strictEqual(_.lte(__num_top__, __num_top__), __bool_top__);
        assert.strictEqual(_.lte(__num_top__, __num_top__), __bool_top__);
        assert.strictEqual(_.lte(__str_top__, __str_top__), __bool_top__);
        assert.strictEqual(_.lte(__str_top__, __str_top__), __bool_top__);
    });
    QUnit.test('should return `false` if `value` > `other`', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.lt(__num_top__, __num_top__), __bool_top__);
        assert.strictEqual(_.lt(__str_top__, __str_top__), __bool_top__);
    });
}());