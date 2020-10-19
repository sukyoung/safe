QUnit.module('lodash.gt');
(function () {
    QUnit.test('should return `true` if `value` > `other`', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.gt(__num_top__, __num_top__), __bool_top__);
        assert.strictEqual(_.gt(__str_top__, __str_top__), __bool_top__);
    });
    QUnit.test('should return `false` if `value` is <= `other`', function (assert) {
        assert.expect(4);
        assert.strictEqual(_.gt(__num_top__, __num_top__), __bool_top__);
        assert.strictEqual(_.gt(__num_top__, __num_top__), __bool_top__);
        assert.strictEqual(_.gt(__str_top__, __str_top__), __bool_top__);
        assert.strictEqual(_.gt(__str_top__, __str_top__), __bool_top__);
    });
}());