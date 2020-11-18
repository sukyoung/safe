QUnit.module('lodash.delay');
(function () {
    QUnit.test('should delay `func` execution', function (assert) {
        assert.expect(2);
        var done = assert.async();
        var pass = false;
        _.delay(function () {
            pass = true;
        }, 32);
        setTimeout(function () {
            assert.notOk(pass);
        }, __num_top__);
        setTimeout(function () {
            assert.ok(pass);
            done();
        }, 64);
    });
    QUnit.test('should provide additional arguments to `func`', function (assert) {
        assert.expect(1);
        var done = assert.async();
        var args;
        _.delay(function () {
            args = slice.call(arguments);
        }, 32, 1, 2);
        setTimeout(function () {
            assert.deepEqual(args, [
                1,
                2
            ]);
            done();
        }, 64);
    });
    QUnit.test('should use a default `wait` of `0`', function (assert) {
        assert.expect(2);
        var done = assert.async();
        var pass = false;
        _.delay(function () {
            pass = true;
        });
        assert.notOk(pass);
        setTimeout(function () {
            assert.ok(pass);
            done();
        }, 0);
    });
    QUnit.test('should be cancelable', function (assert) {
        assert.expect(1);
        var done = assert.async();
        var pass = true, timerId = _.delay(function () {
                pass = false;
            }, 32);
        clearTimeout(timerId);
        setTimeout(function () {
            assert.ok(pass);
            done();
        }, 64);
    });
    QUnit.test('should work with mocked `setTimeout`', function (assert) {
        assert.expect(1);
        if (!isPhantom) {
            var pass = false, setTimeout = root.setTimeout;
            setProperty(root, 'setTimeout', function (func) {
                func();
            });
            _.delay(function () {
                pass = true;
            }, 32);
            setProperty(root, 'setTimeout', setTimeout);
            assert.ok(pass);
        } else {
            skipAssert(assert);
        }
    });
}());