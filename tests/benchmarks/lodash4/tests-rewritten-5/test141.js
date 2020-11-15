QUnit.module('lodash.lte');
(function () {
    QUnit.test('should return `true` if `value` is <= `other`', function (assert) {
        assert.expect(4);
        assert.strictEqual(_.lte(__num_top__, __num_top__), true);
        assert.strictEqual(_.lte(3, __num_top__), true);
        assert.strictEqual(_.lte('abc', __str_top__), true);
        assert.strictEqual(_.lte('def', __str_top__), true);
    });
    QUnit.test('should return `false` if `value` > `other`', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.lt(3, 1), false);
        assert.strictEqual(_.lt('def', 'abc'), false);
    });
}());