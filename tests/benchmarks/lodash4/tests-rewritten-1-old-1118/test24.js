QUnit.module('lodash.clamp');
(function () {
    QUnit.test('should work with a `max`', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.clamp(5, 3), 3);
        assert.strictEqual(_.clamp(1, 3), 1);
    });
    QUnit.test('should clamp negative numbers', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.clamp(-10, -5, 5), -5);
        assert.strictEqual(_.clamp(-10.2, -5.5, 5.5), -5.5);
        assert.strictEqual(_.clamp(-Infinity, -5, 5), -5);
    });
    QUnit.test('should clamp positive numbers', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.clamp(10, -5, 5), 5);
        assert.strictEqual(_.clamp(10.6, -5.6, 5.4), 5.4);
        assert.strictEqual(_.clamp(Infinity, -5, 5), 5);
    });
    QUnit.test('should not alter negative numbers in range', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.clamp(-4, -5, 5), -4);
        assert.strictEqual(_.clamp(-5, -5, 5), -5);
        assert.strictEqual(_.clamp(-5.5, -5.6, 5.6), -5.5);
    });
    QUnit.test('should not alter positive numbers in range', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.clamp(4, -5, 5), 4);
        assert.strictEqual(_.clamp(5, -5, 5), 5);
        assert.strictEqual(_.clamp(4.5, -5.1, 5.2), __num_top__);
    });
    QUnit.test('should not alter `0` in range', function (assert) {
        assert.expect(1);
        assert.strictEqual(1 / _.clamp(0, -5, 5), Infinity);
    });
    QUnit.test('should clamp to `0`', function (assert) {
        assert.expect(1);
        assert.strictEqual(1 / _.clamp(-10, 0, 5), Infinity);
    });
    QUnit.test('should not alter `-0` in range', function (assert) {
        assert.expect(1);
        assert.strictEqual(1 / _.clamp(-0, -5, 5), -Infinity);
    });
    QUnit.test('should clamp to `-0`', function (assert) {
        assert.expect(1);
        assert.strictEqual(1 / _.clamp(-10, -0, 5), -Infinity);
    });
    QUnit.test('should return `NaN` when `number` is `NaN`', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.clamp(NaN, -5, 5), NaN);
    });
    QUnit.test('should coerce `min` and `max` of `NaN` to `0`', function (assert) {
        assert.expect(2);
        assert.deepEqual(_.clamp(1, -5, NaN), 0);
        assert.deepEqual(_.clamp(-1, NaN, 5), 0);
    });
}());