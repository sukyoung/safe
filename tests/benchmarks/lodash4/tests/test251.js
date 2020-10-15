QUnit.module('lodash.toUpper');

(function() {
  QUnit.test('should convert whole string to upper case', function(assert) {
    assert.expect(3);

    assert.deepEqual(_.toUpper('--Foo-Bar'), '--FOO-BAR');
    assert.deepEqual(_.toUpper('fooBar'), 'FOOBAR');
    assert.deepEqual(_.toUpper('__FOO_BAR__'), '__FOO_BAR__');
  });
}());