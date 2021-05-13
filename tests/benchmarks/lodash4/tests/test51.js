QUnit.module('lodash.dropWhile');

(function() {
  var array = [1, 2, 3, 4];

  var objects = [
    { 'a': 2, 'b': 2 },
    { 'a': 1, 'b': 1 },
    { 'a': 0, 'b': 0 }
  ];

  QUnit.test('should drop elements while `predicate` returns truthy', function(assert) {
    assert.expect(1);

    var actual = _.dropWhile(array, function(n) {
      return n < 3;
    });

    assert.deepEqual(actual, [3, 4]);
  });

  QUnit.test('should provide correct `predicate` arguments', function(assert) {
    assert.expect(1);

    var args;

    _.dropWhile(array, function() {
      args = slice.call(arguments);
    });

    assert.deepEqual(args, [1, 0, array]);
  });

  QUnit.test('should work with `_.matches` shorthands', function(assert) {
    assert.expect(1);

    assert.deepEqual(_.dropWhile(objects, { 'b': 2 }), objects.slice(1));
  });

  QUnit.test('should work with `_.matchesProperty` shorthands', function(assert) {
    assert.expect(1);

    assert.deepEqual(_.dropWhile(objects, ['b', 2]), objects.slice(1));
  });

  QUnit.test('should work with `_.property` shorthands', function(assert) {
    assert.expect(1);

    assert.deepEqual(_.dropWhile(objects, 'b'), objects.slice(2));
  });

  QUnit.test('should work in a lazy sequence', function(assert) {
    assert.expect(3);

    if (!isNpm) {
      var array = lodashStable.range(1, LARGE_ARRAY_SIZE + 3),
          predicate = function(n) { return n < 3; },
          expected = _.dropWhile(array, predicate),
          wrapped = _(array).dropWhile(predicate);

      assert.deepEqual(wrapped.value(), expected);
      assert.deepEqual(wrapped.reverse().value(), expected.slice().reverse());
      assert.strictEqual(wrapped.last(), _.last(expected));
    }
    else {
      skipAssert(assert, 3);
    }
  });

  QUnit.test('should work in a lazy sequence with `drop`', function(assert) {
    assert.expect(1);

    if (!isNpm) {
      var array = lodashStable.range(1, LARGE_ARRAY_SIZE + 3);

      var actual = _(array)
        .dropWhile(function(n) { return n == 1; })
        .drop()
        .dropWhile(function(n) { return n == 3; })
        .value();

      assert.deepEqual(actual, array.slice(3));
    }
    else {
      skipAssert(assert);
    }
  });
}());