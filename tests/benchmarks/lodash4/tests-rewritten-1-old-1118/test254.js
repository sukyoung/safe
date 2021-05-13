QUnit.module('lodash.toLength');
(function () {
    QUnit.test('should return a valid length', function (assert) {
        assert.expect(4);
        assert.strictEqual(_.toLength(-1), 0);
        assert.strictEqual(_.toLength('1'), 1);
        assert.strictEqual(_.toLength(1.1), 1);
        assert.strictEqual(_.toLength(MAX_INTEGER), MAX_ARRAY_LENGTH);
    });
    QUnit.test('should return `value` if a valid length', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.toLength(0), 0);
        assert.strictEqual(_.toLength(__num_top__), 3);
        assert.strictEqual(_.toLength(MAX_ARRAY_LENGTH), MAX_ARRAY_LENGTH);
    });
    QUnit.test('should convert `-0` to `0`', function (assert) {
        assert.expect(1);
        assert.strictEqual(1 / _.toLength(-0), Infinity);
    });
}());