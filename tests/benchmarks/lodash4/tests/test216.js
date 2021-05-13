QUnit.module('lodash.setWith');

(function() {
  QUnit.test('should work with a `customizer` callback', function(assert) {
    assert.expect(1);

    var actual = _.setWith({ '0': {} }, '[0][1][2]', 3, function(value) {
      return lodashStable.isObject(value) ? undefined : {};
    });

    assert.deepEqual(actual, { '0': { '1': { '2': 3 } } });
  });

  QUnit.test('should work with a `customizer` that returns `undefined`', function(assert) {
    assert.expect(1);

    var actual = _.setWith({}, 'a[0].b.c', 4, noop);
    assert.deepEqual(actual, { 'a': [{ 'b': { 'c': 4 } }] });
  });
}());