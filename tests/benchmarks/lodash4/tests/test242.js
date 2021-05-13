QUnit.module('lodash.takeWhile');

(function() {
  var array = [1, 2, 3, 4];

  var objects = [
    { 'a': 2, 'b': 2 },
    { 'a': 1, 'b': 1 },
    { 'a': 0, 'b': 0 }
  ];

  QUnit.test('should take elements while `predicate` returns truthy', function(assert) {
    assert.expect(1);

    var actual = _.takeWhile(array, function(n) {
      return n < 3;
    });

    assert.deepEqual(actual, [1, 2]);
  });

  QUnit.test('should provide correct `predicate` arguments', function(assert) {
    assert.expect(1);

    var args;

    _.takeWhile(array, function() {
      args = slice.call(arguments);
    });

    assert.deepEqual(args, [1, 0, array]);
  });

  QUnit.test('should work with `_.matches` shorthands', function(assert) {
    assert.expect(1);

    assert.deepEqual(_.takeWhile(objects, { 'b': 2 }), objects.slice(0, 1));
  });

  QUnit.test('should work with `_.matchesProperty` shorthands', function(assert) {
    assert.expect(1);

    assert.deepEqual(_.takeWhile(objects, ['b', 2]), objects.slice(0, 1));
  });
  QUnit.test('should work with `_.property` shorthands', function(assert) {
    assert.expect(1);

    assert.deepEqual(_.takeWhile(objects, 'b'), objects.slice(0, 2));
  });

  QUnit.test('should work in a lazy sequence', function(assert) {
    assert.expect(3);

    if (!isNpm) {
      var array = lodashStable.range(LARGE_ARRAY_SIZE),
          predicate = function(n) { return n < 3; },
          expected = _.takeWhile(array, predicate),
          wrapped = _(array).takeWhile(predicate);

      assert.deepEqual(wrapped.value(), expected);
      assert.deepEqual(wrapped.reverse().value(), expected.slice().reverse());
      assert.strictEqual(wrapped.last(), _.last(expected));
    }
    else {
      skipAssert(assert, 3);
    }
  });

  QUnit.test('should work in a lazy sequence with `take`', function(assert) {
    assert.expect(1);

    if (!isNpm) {
      var array = lodashStable.range(LARGE_ARRAY_SIZE);

      var actual = _(array)
        .takeWhile(function(n) { return n < 4; })
        .take(2)
        .takeWhile(function(n) { return n == 0; })
        .value();

      assert.deepEqual(actual, [0]);
    }
    else {
      skipAssert(assert);
    }
  });

  QUnit.test('should provide correct `predicate` arguments in a lazy sequence', function(assert) {
    assert.expect(5);

    if (!isNpm) {
      var args,
          array = lodashStable.range(LARGE_ARRAY_SIZE + 1),
          expected = [1, 0, lodashStable.map(array.slice(1), square)];

      _(array).slice(1).takeWhile(function(value, index, array) {
        args = slice.call(arguments);
      }).value();

      assert.deepEqual(args, [1, 0, array.slice(1)]);

      _(array).slice(1).map(square).takeWhile(function(value, index, array) {
        args = slice.call(arguments);
      }).value();

      assert.deepEqual(args, expected);

      _(array).slice(1).map(square).takeWhile(function(value, index) {
        args = slice.call(arguments);
      }).value();

      assert.deepEqual(args, expected);

      _(array).slice(1).map(square).takeWhile(function(value) {
        args = slice.call(arguments);
      }).value();

      assert.deepEqual(args, [1]);

      _(array).slice(1).map(square).takeWhile(function() {
        args = slice.call(arguments);
      }).value();

      assert.deepEqual(args, expected);
    }
    else {
      skipAssert(assert, 5);
    }
  });
}());