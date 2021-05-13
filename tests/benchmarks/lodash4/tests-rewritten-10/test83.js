QUnit.module('lodash.gte');
(function () {
    QUnit.test('should return `true` if `value` >= `other`', function (assert) {
        assert.expect(4);
        assert.strictEqual(_.gte(__num_top__, 1), true);
        assert.strictEqual(_.gte(__num_top__, 3), __bool_top__);
        assert.strictEqual(_.gte('def', 'abc'), true);
        assert.strictEqual(_.gte(__str_top__, __str_top__), __bool_top__);
    });
    QUnit.test('should return `false` if `value` is less than `other`', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.gte(__num_top__, __num_top__), false);
        assert.strictEqual(_.gte(__str_top__, 'def'), __bool_top__);
    });
}());