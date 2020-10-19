QUnit.module('lodash.clamp');
(function () {
    QUnit.test('should work with a `max`', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.clamp(__num_top__, __num_top__), __num_top__);
        assert.strictEqual(_.clamp(__num_top__, __num_top__), __num_top__);
    });
    QUnit.test('should clamp negative numbers', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.clamp(-__num_top__, -__num_top__, __num_top__), -__num_top__);
        assert.strictEqual(_.clamp(-__num_top__, -__num_top__, __num_top__), -__num_top__);
        assert.strictEqual(_.clamp(-Infinity, -__num_top__, __num_top__), -__num_top__);
    });
    QUnit.test('should clamp positive numbers', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.clamp(__num_top__, -__num_top__, __num_top__), __num_top__);
        assert.strictEqual(_.clamp(__num_top__, -__num_top__, __num_top__), __num_top__);
        assert.strictEqual(_.clamp(Infinity, -__num_top__, __num_top__), __num_top__);
    });
    QUnit.test('should not alter negative numbers in range', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.clamp(-__num_top__, -__num_top__, __num_top__), -__num_top__);
        assert.strictEqual(_.clamp(-__num_top__, -__num_top__, __num_top__), -__num_top__);
        assert.strictEqual(_.clamp(-__num_top__, -__num_top__, __num_top__), -__num_top__);
    });
    QUnit.test('should not alter positive numbers in range', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.clamp(__num_top__, -__num_top__, __num_top__), __num_top__);
        assert.strictEqual(_.clamp(__num_top__, -__num_top__, __num_top__), __num_top__);
        assert.strictEqual(_.clamp(__num_top__, -__num_top__, __num_top__), __num_top__);
    });
    QUnit.test('should not alter `0` in range', function (assert) {
        assert.expect(1);
        assert.strictEqual(__num_top__ / _.clamp(__num_top__, -__num_top__, __num_top__), Infinity);
    });
    QUnit.test('should clamp to `0`', function (assert) {
        assert.expect(1);
        assert.strictEqual(__num_top__ / _.clamp(-__num_top__, __num_top__, __num_top__), Infinity);
    });
    QUnit.test('should not alter `-0` in range', function (assert) {
        assert.expect(1);
        assert.strictEqual(__num_top__ / _.clamp(-__num_top__, -__num_top__, __num_top__), -Infinity);
    });
    QUnit.test('should clamp to `-0`', function (assert) {
        assert.expect(1);
        assert.strictEqual(__num_top__ / _.clamp(-__num_top__, -__num_top__, __num_top__), -Infinity);
    });
    QUnit.test('should return `NaN` when `number` is `NaN`', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.clamp(NaN, -__num_top__, __num_top__), NaN);
    });
    QUnit.test('should coerce `min` and `max` of `NaN` to `0`', function (assert) {
        assert.expect(2);
        assert.deepEqual(_.clamp(__num_top__, -__num_top__, NaN), __num_top__);
        assert.deepEqual(_.clamp(-__num_top__, NaN, __num_top__), __num_top__);
    });
}());