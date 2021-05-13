QUnit.module('lodash.assignIn');

(function() {
  QUnit.test('should be aliased', function(assert) {
    assert.expect(1);

    assert.strictEqual(_.extend, _.assignIn);
  });
}());