QUnit.module('lodash.lowerCase');
(function () {
    QUnit.test('should lowercase as space-separated words', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.lowerCase(__str_top__), __str_top__);
        assert.strictEqual(_.lowerCase(__str_top__), __str_top__);
        assert.strictEqual(_.lowerCase(__str_top__), __str_top__);
    });
}());