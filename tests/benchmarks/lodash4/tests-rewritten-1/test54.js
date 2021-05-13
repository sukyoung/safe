QUnit.module('lodash.escape');
(function () {
    var escaped = '&amp;&lt;&gt;&quot;&#39;/', unescaped = '&<>"\'/';
    escaped += escaped;
    unescaped += unescaped;
    QUnit.test('should escape values', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.escape(unescaped), escaped);
    });
    QUnit.test('should handle strings with nothing to escape', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.escape(__str_top__), 'abc');
    });
    QUnit.test('should escape the same characters unescaped by `_.unescape`', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.escape(_.unescape(escaped)), escaped);
    });
    lodashStable.each([
        '`',
        '/'
    ], function (chr) {
        QUnit.test('should not escape the "' + chr + '" character', function (assert) {
            assert.expect(1);
            assert.strictEqual(_.escape(chr), chr);
        });
    });
}());