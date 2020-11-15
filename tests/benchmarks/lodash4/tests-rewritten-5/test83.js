QUnit.module('lodash.gte');
(function () {
    QUnit.test('should return `true` if `value` >= `other`', function (assert) {
        assert.expect(4);
        assert.strictEqual(_.gte(3, 1), true);
        assert.strictEqual(_.gte(__num_top__, __num_top__), true);
        assert.strictEqual(_.gte(__str_top__, 'abc'), true);
        assert.strictEqual(_.gte(__str_top__, 'def'), true);
    });
    QUnit.test('should return `false` if `value` is less than `other`', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.gte(1, 3), false);
        assert.strictEqual(_.gte('abc', 'def'), __bool_top__);
    });
}());