QUnit.module('lodash.includes');

(function() {
  lodashStable.each({
    'an `arguments` object': arguments,
    'an array': [1, 2, 3, 4],
    'an object': { 'a': 1, 'b': 2, 'c': 3, 'd': 4 },
    'a string': '1234'
  },
  function(collection, key) {
    QUnit.test('should work with ' + key + ' and  return `true` for  matched values', function(assert) {
      assert.expect(1);

      assert.strictEqual(_.includes(collection, 3), true);
    });

    QUnit.test('should work with ' + key + ' and  return `false` for unmatched values', function(assert) {
      assert.expect(1);

      assert.strictEqual(_.includes(collection, 5), false);
    });

    QUnit.test('should work with ' + key + ' and floor `position` values', function(assert) {
      assert.expect(1);

      assert.strictEqual(_.includes(collection, 2, 1.2), true);
    });

    QUnit.test('should work with ' + key + ' and return an unwrapped value implicitly when chaining', function(assert) {
      assert.expect(1);

      if (!isNpm) {
        assert.strictEqual(_(collection).includes(3), true);
      }
      else {
        skipAssert(assert);
      }
    });

    QUnit.test('should work with ' + key + ' and return a wrapped value when explicitly chaining', function(assert) {
      assert.expect(1);

      if (!isNpm) {
        assert.ok(_(collection).chain().includes(3) instanceof _);
      }
      else {
        skipAssert(assert);
      }
    });
  });

  lodashStable.each({
    'literal': 'abc',
    'object': Object('abc')
  },
  function(collection, key) {
    QUnit.test('should work with a string ' + key + ' for `collection`', function(assert) {
      assert.expect(2);

      assert.strictEqual(_.includes(collection, 'bc'), true);
      assert.strictEqual(_.includes(collection, 'd'), false);
    });
  });

  QUnit.test('should return `false` for empty collections', function(assert) {
    assert.expect(1);

    var expected = lodashStable.map(empties, stubFalse);

    var actual = lodashStable.map(empties, function(value) {
      try {
        return _.includes(value);
      } catch (e) {}
    });

    assert.deepEqual(actual, expected);
  });

  QUnit.test('should work with a string and a `fromIndex` >= `length`', function(assert) {
    assert.expect(1);

    var string = '1234',
        length = string.length,
        indexes = [4, 6, Math.pow(2, 32), Infinity];

    var expected = lodashStable.map(indexes, function(index) {
      return [false, false, index == length];
    });

    var actual = lodashStable.map(indexes, function(fromIndex) {
      return [
        _.includes(string, 1, fromIndex),
        _.includes(string, undefined, fromIndex),
        _.includes(string, '', fromIndex)
      ];
    });

    assert.deepEqual(actual, expected);
  });

  QUnit.test('should match `NaN`', function(assert) {
    assert.expect(1);

    assert.strictEqual(_.includes([1, NaN, 3], NaN), true);
  });

  QUnit.test('should match `-0` as `0`', function(assert) {
    assert.expect(2);

    assert.strictEqual(_.includes([-0], 0), true);
    assert.strictEqual(_.includes([0], -0), true);
  });

  QUnit.test('should work as an iteratee for methods like `_.every`', function(assert) {
    assert.expect(1);

    var array = [2, 3, 1],
        values = [1, 2, 3];

    assert.ok(lodashStable.every(values, lodashStable.partial(_.includes, array)));
  });
}(1, 2, 3, 4));