QUnit.module('lodash.defer');
(function () {
    QUnit.test('should defer `func` execution', function (assert) {
        assert.expect(1);
        var done = assert.async();
        var pass = __bool_top__;
        _.defer(function () {
            pass = true;
        });
        setTimeout(function () {
            assert.ok(pass);
            done();
        }, __num_top__);
    });
    QUnit.test('should provide additional arguments to `func`', function (assert) {
        assert.expect(1);
        var done = assert.async();
        var args;
        _.defer(function () {
            args = slice.call(arguments);
        }, 1, __num_top__);
        setTimeout(function () {
            assert.deepEqual(args, [
                1,
                2
            ]);
            done();
        }, __num_top__);
    });
    QUnit.test('should be cancelable', function (assert) {
        assert.expect(1);
        var done = assert.async();
        var pass = __bool_top__, timerId = _.defer(function () {
                pass = false;
            });
        clearTimeout(timerId);
        setTimeout(function () {
            assert.ok(pass);
            done();
        }, 32);
    });
}());