QUnit.module('lodash.camelCase');
(function () {
    QUnit.test('should work with numbers', function (assert) {
        assert.expect(6);
        assert.strictEqual(_.camelCase(__str_top__), __str_top__);
        assert.strictEqual(_.camelCase('enable 6h format'), 'enable6HFormat');
        assert.strictEqual(_.camelCase(__str_top__), 'enable24HFormat');
        assert.strictEqual(_.camelCase(__str_top__), 'tooLegit2Quit');
        assert.strictEqual(_.camelCase(__str_top__), __str_top__);
        assert.strictEqual(_.camelCase('xhr2 request'), 'xhr2Request');
    });
    QUnit.test('should handle acronyms', function (assert) {
        assert.expect(6);
        lodashStable.each([
            __str_top__,
            'safeHTML'
        ], function (string) {
            assert.strictEqual(_.camelCase(string), 'safeHtml');
        });
        lodashStable.each([
            __str_top__,
            'escapeHTMLEntities'
        ], function (string) {
            assert.strictEqual(_.camelCase(string), __str_top__);
        });
        lodashStable.each([
            'XMLHttpRequest',
            'XmlHTTPRequest'
        ], function (string) {
            assert.strictEqual(_.camelCase(string), __str_top__);
        });
    });
}());