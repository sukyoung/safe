QUnit.module('lodash.updateWith');

(function() {
  QUnit.test('should work with a `customizer` callback', function(assert) {
    assert.expect(1);

    var actual = _.updateWith({ '0': {} }, '[0][1][2]', stubThree, function(value) {
      return lodashStable.isObject(value) ? undefined : {};
    });

    assert.deepEqual(actual, { '0': { '1': { '2': 3 } } });
  });

  QUnit.test('should work with a `customizer` that returns `undefined`', function(assert) {
    assert.expect(1);

    var actual = _.updateWith({}, 'a[0].b.c', stubFour, noop);
    assert.deepEqual(actual, { 'a': [{ 'b': { 'c': 4 } }] });
  });
}());