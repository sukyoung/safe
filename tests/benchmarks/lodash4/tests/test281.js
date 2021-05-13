QUnit.module('values methods');

lodashStable.each(['values', 'valuesIn'], function(methodName) {
  var func = _[methodName],
      isValues = methodName == 'values';

  QUnit.test('`_.' + methodName + '` should get string keyed values of `object`', function(assert) {
    assert.expect(1);

    var object = { 'a': 1, 'b': 2 },
        actual = func(object).sort();

    assert.deepEqual(actual, [1, 2]);
  });

  QUnit.test('`_.' + methodName + '` should work with an object that has a `length` property', function(assert) {
    assert.expect(1);

    var object = { '0': 'a', '1': 'b', 'length': 2 },
        actual = func(object).sort();

    assert.deepEqual(actual, [2, 'a', 'b']);
  });

  QUnit.test('`_.' + methodName + '` should ' + (isValues ? 'not ' : '') + 'include inherited string keyed property values', function(assert) {
    assert.expect(1);

    function Foo() {
      this.a = 1;
    }
    Foo.prototype.b = 2;

    var expected = isValues ? [1] : [1, 2],
        actual = func(new Foo).sort();

    assert.deepEqual(actual, expected);
  });

  QUnit.test('`_.' + methodName + '` should work with `arguments` objects', function(assert) {
    assert.expect(1);

    var values = [args, strictArgs],
        expected = lodashStable.map(values, lodashStable.constant([1, 2, 3]));

    var actual = lodashStable.map(values, function(value) {
      return func(value).sort();
    });

    assert.deepEqual(actual, expected);
  });
});