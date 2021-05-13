QUnit.module('lodash.escape');
(function () {
    var escaped = __str_top__, unescaped = __str_top__;
    escaped += escaped;
    unescaped += unescaped;
    QUnit.test('should escape values', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.escape(unescaped), escaped);
    });
    QUnit.test('should handle strings with nothing to escape', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.escape(__str_top__), __str_top__);
    });
    QUnit.test('should escape the same characters unescaped by `_.unescape`', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.escape(_.unescape(escaped)), escaped);
    });
    lodashStable.each([
        __str_top__,
        __str_top__
    ], function (chr) {
        QUnit.test(__str_top__ + chr + __str_top__, function (assert) {
            assert.expect(1);
            assert.strictEqual(_.escape(chr), chr);
        });
    });
}());