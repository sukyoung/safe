QUnit.module('lodash.gte');
(function () {
    QUnit.test('should return `true` if `value` >= `other`', function (assert) {
        assert.expect(4);
        assert.strictEqual(_.gte(__num_top__, __num_top__), __bool_top__);
        assert.strictEqual(_.gte(3, __num_top__), true);
        assert.strictEqual(_.gte(__str_top__, 'abc'), true);
        assert.strictEqual(_.gte('def', __str_top__), true);
    });
    QUnit.test('should return `false` if `value` is less than `other`', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.gte(__num_top__, 3), __bool_top__);
        assert.strictEqual(_.gte('abc', __str_top__), __bool_top__);
    });
}());