QUnit.module('lodash(...) methods that return the wrapped modified array');

(function() {
  var funcs = [
    'push',
    'reverse',
    'sort',
    'unshift'
  ];

  lodashStable.each(funcs, function(methodName) {
    QUnit.test('`_(...).' + methodName + '` should return a new wrapper', function(assert) {
      assert.expect(2);

      if (!isNpm) {
        var array = [1, 2, 3],
            wrapped = _(array),
            actual = wrapped[methodName]();

        assert.ok(actual instanceof _);
        assert.notStrictEqual(actual, wrapped);
      }
      else {
        skipAssert(assert, 2);
      }
    });
  });
}());