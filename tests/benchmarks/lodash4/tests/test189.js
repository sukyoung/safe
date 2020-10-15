QUnit.module('pick methods');

lodashStable.each(['pick', 'pickBy'], function(methodName) {
  var expected = { 'a': 1, 'c': 3 },
      func = _[methodName],
      isPick = methodName == 'pick',
      object = { 'a': 1, 'b': 2, 'c': 3, 'd': 4 },
      resolve = lodashStable.nthArg(1);

  if (methodName == 'pickBy') {
    resolve = function(object, props) {
      props = lodashStable.castArray(props);
      return function(value) {
        return lodashStable.some(props, function(key) {
          key = lodashStable.isSymbol(key) ? key : lodashStable.toString(key);
          return object[key] === value;
        });
      };
    };
  }
  QUnit.test('`_.' + methodName + '` should create an object of picked string keyed properties', function(assert) {
    assert.expect(2);

    assert.deepEqual(func(object, resolve(object, 'a')), { 'a': 1 });
    assert.deepEqual(func(object, resolve(object, ['a', 'c'])), expected);
  });

  QUnit.test('`_.' + methodName + '` should pick inherited string keyed properties', function(assert) {
    assert.expect(1);

    function Foo() {}
    Foo.prototype = object;

    var foo = new Foo;
    assert.deepEqual(func(foo, resolve(foo, ['a', 'c'])), expected);
  });

  QUnit.test('`_.' + methodName + '` should preserve the sign of `0`', function(assert) {
    assert.expect(1);

    var object = { '-0': 'a', '0': 'b' },
        props = [-0, Object(-0), 0, Object(0)],
        expected = [{ '-0': 'a' }, { '-0': 'a' }, { '0': 'b' }, { '0': 'b' }];

    var actual = lodashStable.map(props, function(key) {
      return func(object, resolve(object, key));
    });

    assert.deepEqual(actual, expected);
  });

  QUnit.test('`_.' + methodName + '` should pick symbols', function(assert) {
    assert.expect(3);

    function Foo() {
      this[symbol] = 1;
    }

    if (Symbol) {
      var symbol2 = Symbol('b');
      Foo.prototype[symbol2] = 2;

      var symbol3 = Symbol('c');
      defineProperty(Foo.prototype, symbol3, {
        'configurable': true,
        'enumerable': false,
        'writable': true,
        'value': 3
      });

      var foo = new Foo,
          actual = func(foo, resolve(foo, [symbol, symbol2, symbol3]));

      assert.strictEqual(actual[symbol], 1);
      assert.strictEqual(actual[symbol2], 2);

      if (isPick) {
        assert.strictEqual(actual[symbol3], 3);
      } else {
        assert.notOk(symbol3 in actual);
      }
    }
    else {
      skipAssert(assert, 3);
    }
  });

  QUnit.test('`_.' + methodName + '` should work with an array `object`', function(assert) {
    assert.expect(1);

    var array = [1, 2, 3];
    assert.deepEqual(func(array, resolve(array, '1')), { '1': 2 });
  });
});