QUnit.module('lodash.unescape');

(function() {
  var escaped = '&amp;&lt;&gt;&quot;&#39;/',
      unescaped = '&<>"\'/';

  escaped += escaped;
  unescaped += unescaped;

  QUnit.test('should unescape entities in order', function(assert) {
    assert.expect(1);

    assert.strictEqual(_.unescape('&amp;lt;'), '&lt;');
  });

  QUnit.test('should unescape the proper entities', function(assert) {
    assert.expect(1);

    assert.strictEqual(_.unescape(escaped), unescaped);
  });

  QUnit.test('should handle strings with nothing to unescape', function(assert) {
    assert.expect(1);

    assert.strictEqual(_.unescape('abc'), 'abc');
  });

  QUnit.test('should unescape the same characters escaped by `_.escape`', function(assert) {
    assert.expect(1);

    assert.strictEqual(_.unescape(_.escape(unescaped)), unescaped);
  });

  lodashStable.each(['&#96;', '&#x2F;'], function(entity) {
    QUnit.test('should not unescape the "' + entity + '" entity', function(assert) {
      assert.expect(1);

      assert.strictEqual(_.unescape(entity), entity);
    });
  });
}());