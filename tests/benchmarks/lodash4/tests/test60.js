lodashStable.each(['find', 'findIndex', 'findKey', 'findLast', 'findLastIndex', 'findLastKey'], function(methodName) {
  QUnit.module('lodash.' + methodName);

  var array = [1, 2, 3, 4],
      func = _[methodName];

  var objects = [
    { 'a': 0, 'b': 0 },
    { 'a': 1, 'b': 1 },
    { 'a': 2, 'b': 2 }
  ];

  var expected = ({
    'find': [objects[1], undefined, objects[2]],
    'findIndex': [1, -1, 2],
    'findKey': ['1', undefined, '2'],
    'findLast': [objects[2], undefined, objects[2]],
    'findLastIndex': [2, -1, 2],
    'findLastKey': ['2', undefined, '2']
  })[methodName];

  QUnit.test('`_.' + methodName + '` should return the found value', function(assert) {
    assert.expect(1);

    assert.strictEqual(func(objects, function(object) { return object.a; }), expected[0]);
  });

  QUnit.test('`_.' + methodName + '` should return `' + expected[1] + '` if value is not found', function(assert) {
    assert.expect(1);

    assert.strictEqual(func(objects, function(object) { return object.a === 3; }), expected[1]);
  });

  QUnit.test('`_.' + methodName + '` should work with `_.matches` shorthands', function(assert) {
    assert.expect(1);

    assert.strictEqual(func(objects, { 'b': 2 }), expected[2]);
  });

  QUnit.test('`_.' + methodName + '` should work with `_.matchesProperty` shorthands', function(assert) {
    assert.expect(1);

    assert.strictEqual(func(objects, ['b', 2]), expected[2]);
  });

  QUnit.test('`_.' + methodName + '` should work with `_.property` shorthands', function(assert) {
    assert.expect(1);

    assert.strictEqual(func(objects, 'b'), expected[0]);
  });

  QUnit.test('`_.' + methodName + '` should return `' + expected[1] + '` for empty collections', function(assert) {
    assert.expect(1);

    var emptyValues = lodashStable.endsWith(methodName, 'Index') ? lodashStable.reject(empties, lodashStable.isPlainObject) : empties,
        expecting = lodashStable.map(emptyValues, lodashStable.constant(expected[1]));

    var actual = lodashStable.map(emptyValues, function(value) {
      try {
        return func(value, { 'a': 3 });
      } catch (e) {}
    });

    assert.deepEqual(actual, expecting);
  });

  QUnit.test('`_.' + methodName + '` should return an unwrapped value when implicitly chaining', function(assert) {
    assert.expect(1);

    var expected = ({
      'find': 1,
      'findIndex': 0,
      'findKey': '0',
      'findLast': 4,
      'findLastIndex': 3,
      'findLastKey': '3'
    })[methodName];

    if (!isNpm) {
      assert.strictEqual(_(array)[methodName](), expected);
    }
    else {
      skipAssert(assert);
    }
  });

  QUnit.test('`_.' + methodName + '` should return a wrapped value when explicitly chaining', function(assert) {
    assert.expect(1);

    if (!isNpm) {
      assert.ok(_(array).chain()[methodName]() instanceof _);
    }
    else {
      skipAssert(assert);
    }
  });

  QUnit.test('`_.' + methodName + '` should not execute immediately when explicitly chaining', function(assert) {
    assert.expect(1);

    if (!isNpm) {
      var wrapped = _(array).chain()[methodName]();
      assert.strictEqual(wrapped.__wrapped__, array);
    }
    else {
      skipAssert(assert);
    }
  });

  QUnit.test('`_.' + methodName + '` should work in a lazy sequence', function(assert) {
    assert.expect(2);

    if (!isNpm) {
      var largeArray = lodashStable.range(1, LARGE_ARRAY_SIZE + 1),
          smallArray = array;

      lodashStable.times(2, function(index) {
        var array = index ? largeArray : smallArray,
            wrapped = _(array).filter(isEven);

        assert.strictEqual(wrapped[methodName](), func(lodashStable.filter(array, isEven)));
      });
    }
    else {
      skipAssert(assert, 2);
    }
  });
});

_.each(['find', 'findIndex', 'findLast', 'findLastIndex'], function(methodName) {
  var func = _[methodName];

  QUnit.test('`_.' + methodName + '` should provide correct `predicate` arguments for arrays', function(assert) {
    assert.expect(1);

    var args,
        array = ['a'];

    func(array, function() {
      args || (args = slice.call(arguments));
    });

    assert.deepEqual(args, ['a', 0, array]);
  });
});

_.each(['find', 'findKey', 'findLast', 'findLastKey'], function(methodName) {
  var func = _[methodName];

  QUnit.test('`_.' + methodName + '` should work with an object for `collection`', function(assert) {
    assert.expect(1);

    var actual = func({ 'a': 1, 'b': 2, 'c': 3 }, function(n) {
      return n < 3;
    });

    var expected = ({
      'find': 1,
      'findKey': 'a',
      'findLast': 2,
      'findLastKey': 'b'
    })[methodName];

    assert.strictEqual(actual, expected);
  });

  QUnit.test('`_.' + methodName + '` should provide correct `predicate` arguments for objects', function(assert) {
    assert.expect(1);

    var args,
        object = { 'a': 1 };

    func(object, function() {
      args || (args = slice.call(arguments));
    });

    assert.deepEqual(args, [1, 'a', object]);
  });
});