QUnit.module('lodash.setWith');
(function () {
    QUnit.test('should work with a `customizer` callback', function (assert) {
        assert.expect(1);
        var actual = _.setWith({ '0': {} }, __str_top__, __num_top__, function (value) {
            return lodashStable.isObject(value) ? undefined : {};
        });
        assert.deepEqual(actual, { '0': { '1': { '2': __num_top__ } } });
    });
    QUnit.test('should work with a `customizer` that returns `undefined`', function (assert) {
        assert.expect(1);
        var actual = _.setWith({}, __str_top__, __num_top__, noop);
        assert.deepEqual(actual, { 'a': [{ 'b': { 'c': __num_top__ } }] });
    });
}());