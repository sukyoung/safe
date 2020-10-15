QUnit.module('memoizeCapped');

(function() {
  var func = _._memoizeCapped;

  QUnit.test('should enforce a max cache size of `MAX_MEMOIZE_SIZE`', function(assert) {
    assert.expect(2);

    if (func) {
      var memoized = func(identity),
          cache = memoized.cache;

      lodashStable.times(MAX_MEMOIZE_SIZE, memoized);
      assert.strictEqual(cache.size, MAX_MEMOIZE_SIZE);

      memoized(MAX_MEMOIZE_SIZE);
      assert.strictEqual(cache.size, 1);
    }
    else {
      skipAssert(assert, 2);
    }
  });
}());