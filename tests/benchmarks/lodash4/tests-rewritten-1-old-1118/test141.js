QUnit.module('lodash.lte');
(function () {
    QUnit.test('should return `true` if `value` is <= `other`', function (assert) {
        assert.expect(4);
        assert.strictEqual(_.lte(1, 3), true);
        assert.strictEqual(_.lte(3, 3), true);
        assert.strictEqual(_.lte('abc', 'def'), true);
        assert.strictEqual(_.lte(__str_top__, 'def'), true);
    });
    QUnit.test('should return `false` if `value` > `other`', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.lt(3, 1), false);
        assert.strictEqual(_.lt('def', 'abc'), false);
    });
}());