QUnit.module('lodash.isEqualWith');

(function() {
  QUnit.test('should provide correct `customizer` arguments', function(assert) {
    assert.expect(1);

    var argsList = [],
        object1 = { 'a': [1, 2], 'b': null },
        object2 = { 'a': [1, 2], 'b': null };

    object1.b = object2;
    object2.b = object1;

    var expected = [
      [object1, object2],
      [object1.a, object2.a, 'a', object1, object2],
      [object1.a[0], object2.a[0], 0, object1.a, object2.a],
      [object1.a[1], object2.a[1], 1, object1.a, object2.a],
      [object1.b, object2.b, 'b', object1.b, object2.b]
    ];

    _.isEqualWith(object1, object2, function(assert) {
      var length = arguments.length,
          args = slice.call(arguments, 0, length - (length > 2 ? 1 : 0));

      argsList.push(args);
    });

    assert.deepEqual(argsList, expected);
  });

  QUnit.test('should handle comparisons when `customizer` returns `undefined`', function(assert) {
    assert.expect(3);

    assert.strictEqual(_.isEqualWith('a', 'a', noop), true);
    assert.strictEqual(_.isEqualWith(['a'], ['a'], noop), true);
    assert.strictEqual(_.isEqualWith({ '0': 'a' }, { '0': 'a' }, noop), true);
  });

  QUnit.test('should not handle comparisons when `customizer` returns `true`', function(assert) {
    assert.expect(3);

    var customizer = function(value) {
      return _.isString(value) || undefined;
    };

    assert.strictEqual(_.isEqualWith('a', 'b', customizer), true);
    assert.strictEqual(_.isEqualWith(['a'], ['b'], customizer), true);
    assert.strictEqual(_.isEqualWith({ '0': 'a' }, { '0': 'b' }, customizer), true);
  });

  QUnit.test('should not handle comparisons when `customizer` returns `false`', function(assert) {
    assert.expect(3);

    var customizer = function(value) {
      return _.isString(value) ? false : undefined;
    };

    assert.strictEqual(_.isEqualWith('a', 'a', customizer), false);
    assert.strictEqual(_.isEqualWith(['a'], ['a'], customizer), false);
    assert.strictEqual(_.isEqualWith({ '0': 'a' }, { '0': 'a' }, customizer), false);
  });

  QUnit.test('should return a boolean value even when `customizer` does not', function(assert) {
    assert.expect(2);

    var actual = _.isEqualWith('a', 'b', stubC);
    assert.strictEqual(actual, true);

    var values = _.without(falsey, undefined),
        expected = lodashStable.map(values, stubFalse);

    actual = [];
    lodashStable.each(values, function(value) {
      actual.push(_.isEqualWith('a', 'a', lodashStable.constant(value)));
    });

    assert.deepEqual(actual, expected);
  });

  QUnit.test('should ensure `customizer` is a function', function(assert) {
    assert.expect(1);

    var array = [1, 2, 3],
        eq = _.partial(_.isEqualWith, array),
        actual = lodashStable.map([array, [1, 0, 3]], eq);

    assert.deepEqual(actual, [true, false]);
  });

  QUnit.test('should call `customizer` for values maps and sets', function(assert) {
    assert.expect(2);

    var value = { 'a': { 'b': 2 } };

    if (Map) {
      var map1 = new Map;
      map1.set('a', value);

      var map2 = new Map;
      map2.set('a', value);
    }
    if (Set) {
      var set1 = new Set;
      set1.add(value);

      var set2 = new Set;
      set2.add(value);
    }
    lodashStable.each([[map1, map2], [set1, set2]], function(pair, index) {
      if (pair[0]) {
        var argsList = [],
            array = lodashStable.toArray(pair[0]);

        var expected = [
          [pair[0], pair[1]],
          [array[0], array[0], 0, array, array],
          [array[0][0], array[0][0], 0, array[0], array[0]],
          [array[0][1], array[0][1], 1, array[0], array[0]]
        ];

        if (index) {
          expected.length = 2;
        }
        _.isEqualWith(pair[0], pair[1], function() {
          var length = arguments.length,
              args = slice.call(arguments, 0, length - (length > 2 ? 1 : 0));

          argsList.push(args);
        });

        assert.deepEqual(argsList, expected, index ? 'Set' : 'Map');
      }
      else {
        skipAssert(assert);
      }
    });
  });
}());