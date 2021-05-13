QUnit.module('lodash.lt');
(function () {
    QUnit.test('should return `true` if `value` is less than `other`', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.lt(1, 3), true);
        assert.strictEqual(_.lt('abc', 'def'), true);
    });
    QUnit.test('should return `false` if `value` >= `other`', function (assert) {
        assert.expect(4);
        assert.strictEqual(_.lt(3, 1), false);
        assert.strictEqual(_.lt(3, 3), false);
        assert.strictEqual(_.lt('def', 'abc'), false);
        assert.strictEqual(_.lt('def', __str_top__), false);
    });
}());