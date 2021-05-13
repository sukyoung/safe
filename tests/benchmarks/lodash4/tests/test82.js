QUnit.module('lodash.gt');

(function() {
  QUnit.test('should return `true` if `value` > `other`', function(assert) {
    assert.expect(2);

    assert.strictEqual(_.gt(3, 1), true);
    assert.strictEqual(_.gt('def', 'abc'), true);
  });

  QUnit.test('should return `false` if `value` is <= `other`', function(assert) {
    assert.expect(4);

    assert.strictEqual(_.gt(1, 3), false);
    assert.strictEqual(_.gt(3, 3), false);
    assert.strictEqual(_.gt('abc', 'def'), false);
    assert.strictEqual(_.gt('def', 'def'), false);
  });
}());