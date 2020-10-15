QUnit.module('lodash.camelCase');

(function() {
  QUnit.test('should work with numbers', function(assert) {
    assert.expect(6);

    assert.strictEqual(_.camelCase('12 feet'), '12Feet');
    assert.strictEqual(_.camelCase('enable 6h format'), 'enable6HFormat');
    assert.strictEqual(_.camelCase('enable 24H format'), 'enable24HFormat');
    assert.strictEqual(_.camelCase('too legit 2 quit'), 'tooLegit2Quit');
    assert.strictEqual(_.camelCase('walk 500 miles'), 'walk500Miles');
    assert.strictEqual(_.camelCase('xhr2 request'), 'xhr2Request');
  });

  QUnit.test('should handle acronyms', function(assert) {
    assert.expect(6);

    lodashStable.each(['safe HTML', 'safeHTML'], function(string) {
      assert.strictEqual(_.camelCase(string), 'safeHtml');
    });

    lodashStable.each(['escape HTML entities', 'escapeHTMLEntities'], function(string) {
      assert.strictEqual(_.camelCase(string), 'escapeHtmlEntities');
    });

    lodashStable.each(['XMLHttpRequest', 'XmlHTTPRequest'], function(string) {
      assert.strictEqual(_.camelCase(string), 'xmlHttpRequest');
    });
  });
}());