QUnit.module('lodash.upperFirst');

(function() {
  QUnit.test('should uppercase only the first character', function(assert) {
    assert.expect(3);

    assert.strictEqual(_.upperFirst('fred'), 'Fred');
    assert.strictEqual(_.upperFirst('Fred'), 'Fred');
    assert.strictEqual(_.upperFirst('FRED'), 'FRED');
  });
}());