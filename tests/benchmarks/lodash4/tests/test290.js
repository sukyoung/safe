QUnit.module('lodash.unzip and lodash.zip');

lodashStable.each(['unzip', 'zip'], function(methodName, index) {
  var func = _[methodName];
  func = lodashStable.bind(index ? func.apply : func.call, func, null);

  var object = {
    'an empty array': [
      [],
      []
    ],
    '0-tuples': [
      [[], []],
      []
    ],
    '2-tuples': [
      [['barney', 'fred'], [36, 40]],
      [['barney', 36], ['fred', 40]]
    ],
    '3-tuples': [
      [['barney', 'fred'], [36, 40], [false, true]],
      [['barney', 36, false], ['fred', 40, true]]
    ]
  };

  lodashStable.forOwn(object, function(pair, key) {
    QUnit.test('`_.' + methodName + '` should work with ' + key, function(assert) {
      assert.expect(2);

      var actual = func(pair[0]);
      assert.deepEqual(actual, pair[1]);
      assert.deepEqual(func(actual), actual.length ? pair[0] : []);
    });
  });

  QUnit.test('`_.' + methodName + '` should work with tuples of different lengths', function(assert) {
    assert.expect(4);

    var pair = [
      [['barney', 36], ['fred', 40, false]],
      [['barney', 'fred'], [36, 40], [undefined, false]]
    ];

    var actual = func(pair[0]);
    assert.ok('0' in actual[2]);
    assert.deepEqual(actual, pair[1]);

    actual = func(actual);
    assert.ok('2' in actual[0]);
    assert.deepEqual(actual, [['barney', 36, undefined], ['fred', 40, false]]);
  });

  QUnit.test('`_.' + methodName + '` should treat falsey values as empty arrays', function(assert) {
    assert.expect(1);

    var expected = lodashStable.map(falsey, stubArray);

    var actual = lodashStable.map(falsey, function(value) {
      return func([value, value, value]);
    });

    assert.deepEqual(actual, expected);
  });

  QUnit.test('`_.' + methodName + '` should ignore values that are not arrays or `arguments` objects', function(assert) {
    assert.expect(1);

    var array = [[1, 2], [3, 4], null, undefined, { '0': 1 }];
    assert.deepEqual(func(array), [[1, 3], [2, 4]]);
  });

  QUnit.test('`_.' + methodName + '` should support consuming its return value', function(assert) {
    assert.expect(1);

    var expected = [['barney', 'fred'], [36, 40]];
    assert.deepEqual(func(func(func(func(expected)))), expected);
  });
});