QUnit.module('lodash.lt');
(function () {
    QUnit.test('should return `true` if `value` is less than `other`', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.lt(1, 3), true);
        assert.strictEqual(_.lt('abc', __str_top__), true);
    });
    QUnit.test('should return `false` if `value` >= `other`', function (assert) {
        assert.expect(4);
        assert.strictEqual(_.lt(3, 1), __bool_top__);
        assert.strictEqual(_.lt(__num_top__, 3), false);
        assert.strictEqual(_.lt('def', __str_top__), __bool_top__);
        assert.strictEqual(_.lt('def', 'def'), false);
    });
}());