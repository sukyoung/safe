QUnit.module('lodash.toPairsIn');

(function() {
  QUnit.test('should be aliased', function(assert) {
    assert.expect(1);

    assert.strictEqual(_.entriesIn, _.toPairsIn);
  });
}());