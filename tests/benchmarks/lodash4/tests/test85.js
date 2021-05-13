QUnit.module('lodash.head');

(function() {
  var array = [1, 2, 3, 4];

  QUnit.test('should return the first element', function(assert) {
    assert.expect(1);

    assert.strictEqual(_.head(array), 1);
  });

  QUnit.test('should return `undefined` when querying empty arrays', function(assert) {
    assert.expect(1);

    arrayProto[0] = 1;
    assert.strictEqual(_.head([]), undefined);
    arrayProto.length = 0;
  });

  QUnit.test('should work as an iteratee for methods like `_.map`', function(assert) {
    assert.expect(1);

    var array = [[1, 2, 3], [4, 5, 6], [7, 8, 9]],
        actual = lodashStable.map(array, _.head);

    assert.deepEqual(actual, [1, 4, 7]);
  });

  QUnit.test('should be aliased', function(assert) {
    assert.expect(1);

    assert.strictEqual(_.first, _.head);
  });

  QUnit.test('should return an unwrapped value when implicitly chaining', function(assert) {
    assert.expect(2);

    if (!isNpm) {
      var wrapped = _(array);
      assert.strictEqual(wrapped.head(), 1);
      assert.strictEqual(wrapped.first(), 1);
    }
    else {
      skipAssert(assert, 2);
    }
  });

  QUnit.test('should return a wrapped value when explicitly chaining', function(assert) {
    assert.expect(2);

    if (!isNpm) {
      var wrapped = _(array).chain();
      assert.ok(wrapped.head() instanceof _);
      assert.ok(wrapped.first() instanceof _);
    }
    else {
      skipAssert(assert, 2);
    }
  });

  QUnit.test('should not execute immediately when explicitly chaining', function(assert) {
    assert.expect(2);

    if (!isNpm) {
      var wrapped = _(array).chain();
      assert.strictEqual(wrapped.head().__wrapped__, array);
      assert.strictEqual(wrapped.first().__wrapped__, array);
    }
    else {
      skipAssert(assert, 2);
    }
  });

  QUnit.test('should work in a lazy sequence', function(assert) {
    assert.expect(4);

    if (!isNpm) {
      var largeArray = lodashStable.range(LARGE_ARRAY_SIZE),
          smallArray = array;

      lodashStable.each(['head', 'first'], function(methodName) {
        lodashStable.times(2, function(index) {
          var array = index ? largeArray : smallArray,
              actual = _(array).filter(isEven)[methodName]();

          assert.strictEqual(actual, _[methodName](_.filter(array, isEven)));
        });
      });
    }
    else {
      skipAssert(assert, 4);
    }
  });
}());