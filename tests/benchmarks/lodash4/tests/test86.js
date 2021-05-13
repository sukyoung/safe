QUnit.module('lodash.identity');

(function() {
  QUnit.test('should return the first argument given', function(assert) {
    assert.expect(1);

    var object = { 'name': 'fred' };
    assert.strictEqual(_.identity(object), object);
  });
}());