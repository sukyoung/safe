QUnit.module('lodash.lt');
(function () {
    QUnit.test('should return `true` if `value` is less than `other`', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.lt(__num_top__, 3), true);
        assert.strictEqual(_.lt('abc', 'def'), __bool_top__);
    });
    QUnit.test('should return `false` if `value` >= `other`', function (assert) {
        assert.expect(4);
        assert.strictEqual(_.lt(3, 1), false);
        assert.strictEqual(_.lt(3, __num_top__), __bool_top__);
        assert.strictEqual(_.lt('def', 'abc'), false);
        assert.strictEqual(_.lt('def', __str_top__), false);
    });
}());