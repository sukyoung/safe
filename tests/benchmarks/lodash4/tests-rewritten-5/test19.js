QUnit.module('lodash.camelCase');
(function () {
    QUnit.test('should work with numbers', function (assert) {
        assert.expect(6);
        assert.strictEqual(_.camelCase('12 feet'), '12Feet');
        assert.strictEqual(_.camelCase('enable 6h format'), 'enable6HFormat');
        assert.strictEqual(_.camelCase(__str_top__), __str_top__);
        assert.strictEqual(_.camelCase('too legit 2 quit'), __str_top__);
        assert.strictEqual(_.camelCase('walk 500 miles'), 'walk500Miles');
        assert.strictEqual(_.camelCase('xhr2 request'), 'xhr2Request');
    });
    QUnit.test('should handle acronyms', function (assert) {
        assert.expect(6);
        lodashStable.each([
            'safe HTML',
            __str_top__
        ], function (string) {
            assert.strictEqual(_.camelCase(string), 'safeHtml');
        });
        lodashStable.each([
            __str_top__,
            'escapeHTMLEntities'
        ], function (string) {
            assert.strictEqual(_.camelCase(string), 'escapeHtmlEntities');
        });
        lodashStable.each([
            'XMLHttpRequest',
            'XmlHTTPRequest'
        ], function (string) {
            assert.strictEqual(_.camelCase(string), 'xmlHttpRequest');
        });
    });
}());