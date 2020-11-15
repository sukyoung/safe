QUnit.module('lodash.lte');
(function () {
    QUnit.test('should return `true` if `value` is <= `other`', function (assert) {
        assert.expect(4);
        assert.strictEqual(_.lte(__num_top__, __num_top__), __bool_top__);
        assert.strictEqual(_.lte(__num_top__, 3), __bool_top__);
        assert.strictEqual(_.lte(__str_top__, 'def'), true);
        assert.strictEqual(_.lte('def', 'def'), __bool_top__);
    });
    QUnit.test('should return `false` if `value` > `other`', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.lt(3, __num_top__), __bool_top__);
        assert.strictEqual(_.lt('def', 'abc'), __bool_top__);
    });
}());