QUnit.module('lodash.camelCase');
(function () {
    QUnit.test('should work with numbers', function (assert) {
        assert.expect(6);
        assert.strictEqual(_.camelCase(__str_top__), __str_top__);
        assert.strictEqual(_.camelCase(__str_top__), __str_top__);
        assert.strictEqual(_.camelCase(__str_top__), __str_top__);
        assert.strictEqual(_.camelCase(__str_top__), __str_top__);
        assert.strictEqual(_.camelCase(__str_top__), __str_top__);
        assert.strictEqual(_.camelCase(__str_top__), __str_top__);
    });
    QUnit.test('should handle acronyms', function (assert) {
        assert.expect(6);
        lodashStable.each([
            __str_top__,
            __str_top__
        ], function (string) {
            assert.strictEqual(_.camelCase(string), __str_top__);
        });
        lodashStable.each([
            __str_top__,
            __str_top__
        ], function (string) {
            assert.strictEqual(_.camelCase(string), __str_top__);
        });
        lodashStable.each([
            __str_top__,
            __str_top__
        ], function (string) {
            assert.strictEqual(_.camelCase(string), __str_top__);
        });
    });
}());