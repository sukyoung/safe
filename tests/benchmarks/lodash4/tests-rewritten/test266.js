QUnit.module('lodash.unescape');
(function () {
    var escaped = __str_top__, unescaped = __str_top__;
    escaped += escaped;
    unescaped += unescaped;
    QUnit.test('should unescape entities in order', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.unescape(__str_top__), __str_top__);
    });
    QUnit.test('should unescape the proper entities', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.unescape(escaped), unescaped);
    });
    QUnit.test('should handle strings with nothing to unescape', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.unescape(__str_top__), __str_top__);
    });
    QUnit.test('should unescape the same characters escaped by `_.escape`', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.unescape(_.escape(unescaped)), unescaped);
    });
    lodashStable.each([
        __str_top__,
        __str_top__
    ], function (entity) {
        QUnit.test(__str_top__ + entity + __str_top__, function (assert) {
            assert.expect(1);
            assert.strictEqual(_.unescape(entity), entity);
        });
    });
}());