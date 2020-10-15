QUnit.module('lodash.sortBy');

(function() {
  var objects = [
    { 'a': 'x', 'b': 3 },
    { 'a': 'y', 'b': 4 },
    { 'a': 'x', 'b': 1 },
    { 'a': 'y', 'b': 2 }
  ];

  QUnit.test('should sort in ascending order by `iteratee`', function(assert) {
    assert.expect(1);

    var actual = lodashStable.map(_.sortBy(objects, function(object) {
      return object.b;
    }), 'b');

    assert.deepEqual(actual, [1, 2, 3, 4]);
  });

  QUnit.test('should use `_.identity` when `iteratee` is nullish', function(assert) {
    assert.expect(1);

    var array = [3, 2, 1],
        values = [, null, undefined],
        expected = lodashStable.map(values, lodashStable.constant([1, 2, 3]));

    var actual = lodashStable.map(values, function(value, index) {
      return index ? _.sortBy(array, value) : _.sortBy(array);
    });

    assert.deepEqual(actual, expected);
  });

  QUnit.test('should work with `_.property` shorthands', function(assert) {
    assert.expect(1);

    var actual = lodashStable.map(_.sortBy(objects.concat(undefined), 'b'), 'b');
    assert.deepEqual(actual, [1, 2, 3, 4, undefined]);
  });

  QUnit.test('should work with an object for `collection`', function(assert) {
    assert.expect(1);

    var actual = _.sortBy({ 'a': 1, 'b': 2, 'c': 3 }, Math.sin);
    assert.deepEqual(actual, [3, 1, 2]);
  });

  QUnit.test('should move `NaN`, nullish, and symbol values to the end', function(assert) {
    assert.expect(2);

    var symbol1 = Symbol ? Symbol('a') : null,
        symbol2 = Symbol ? Symbol('b') : null,
        array = [NaN, undefined, null, 4, symbol1, null, 1, symbol2, undefined, 3, NaN, 2],
        expected = [1, 2, 3, 4, symbol1, symbol2, null, null, undefined, undefined, NaN, NaN];

    assert.deepEqual(_.sortBy(array), expected);

    array = [NaN, undefined, symbol1, null, 'd', null, 'a', symbol2, undefined, 'c', NaN, 'b'];
    expected = ['a', 'b', 'c', 'd', symbol1, symbol2, null, null, undefined, undefined, NaN, NaN];

    assert.deepEqual(_.sortBy(array), expected);
  });

  QUnit.test('should treat number values for `collection` as empty', function(assert) {
    assert.expect(1);

    assert.deepEqual(_.sortBy(1), []);
  });

  QUnit.test('should coerce arrays returned from `iteratee`', function(assert) {
    assert.expect(1);

    var actual = _.sortBy(objects, function(object) {
      var result = [object.a, object.b];
      result.toString = function() { return String(this[0]); };
      return result;
    });

    assert.deepEqual(actual, [objects[0], objects[2], objects[1], objects[3]]);
  });

  QUnit.test('should work as an iteratee for methods like `_.map`', function(assert) {
    assert.expect(1);

    var actual = lodashStable.map([[2, 1, 3], [3, 2, 1]], _.sortBy);
    assert.deepEqual(actual, [[1, 2, 3], [1, 2, 3]]);
  });
}());