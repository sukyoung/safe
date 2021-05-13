QUnit.module('lodash.keyBy');

(function() {
  var array = [
    { 'dir': 'left', 'code': 97 },
    { 'dir': 'right', 'code': 100 }
  ];

  QUnit.test('should transform keys by `iteratee`', function(assert) {
    assert.expect(1);

    var expected = { 'a': { 'dir': 'left', 'code': 97 }, 'd': { 'dir': 'right', 'code': 100 } };

    var actual = _.keyBy(array, function(object) {
      return String.fromCharCode(object.code);
    });

    assert.deepEqual(actual, expected);
  });

  QUnit.test('should use `_.identity` when `iteratee` is nullish', function(assert) {
    assert.expect(1);

    var array = [4, 6, 6],
        values = [, null, undefined],
        expected = lodashStable.map(values, lodashStable.constant({ '4': 4, '6': 6 }));

    var actual = lodashStable.map(values, function(value, index) {
      return index ? _.keyBy(array, value) : _.keyBy(array);
    });

    assert.deepEqual(actual, expected);
  });

  QUnit.test('should work with `_.property` shorthands', function(assert) {
    assert.expect(1);

    var expected = { 'left': { 'dir': 'left', 'code': 97 }, 'right': { 'dir': 'right', 'code': 100 } },
        actual = _.keyBy(array, 'dir');

    assert.deepEqual(actual, expected);
  });

  QUnit.test('should only add values to own, not inherited, properties', function(assert) {
    assert.expect(2);

    var actual = _.keyBy([6.1, 4.2, 6.3], function(n) {
      return Math.floor(n) > 4 ? 'hasOwnProperty' : 'constructor';
    });

    assert.deepEqual(actual.constructor, 4.2);
    assert.deepEqual(actual.hasOwnProperty, 6.3);
  });

  QUnit.test('should work with a number for `iteratee`', function(assert) {
    assert.expect(2);

    var array = [
      [1, 'a'],
      [2, 'a'],
      [2, 'b']
    ];

    assert.deepEqual(_.keyBy(array, 0), { '1': [1, 'a'], '2': [2, 'b'] });
    assert.deepEqual(_.keyBy(array, 1), { 'a': [2, 'a'], 'b': [2, 'b'] });
  });

  QUnit.test('should work with an object for `collection`', function(assert) {
    assert.expect(1);

    var actual = _.keyBy({ 'a': 6.1, 'b': 4.2, 'c': 6.3 }, Math.floor);
    assert.deepEqual(actual, { '4': 4.2, '6': 6.3 });
  });

  QUnit.test('should work in a lazy sequence', function(assert) {
    assert.expect(1);

    if (!isNpm) {
      var array = lodashStable.range(LARGE_ARRAY_SIZE).concat(
        lodashStable.range(Math.floor(LARGE_ARRAY_SIZE / 2), LARGE_ARRAY_SIZE),
        lodashStable.range(Math.floor(LARGE_ARRAY_SIZE / 1.5), LARGE_ARRAY_SIZE)
      );

      var actual = _(array).keyBy().map(square).filter(isEven).take().value();

      assert.deepEqual(actual, _.take(_.filter(_.map(_.keyBy(array), square), isEven)));
    }
    else {
      skipAssert(assert);
    }
  });
}());