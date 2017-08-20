# JSCD: JavaScript Clone Detector

JSCD is an open-source clone detector for JavaScript.

## Usage

Configure the following parameters of ``bin/jscd_config``.
```sh
SRC_DIR='tests/clone/jquery'
MIN_TOKENS='50 100'
STRIDE='2'
SIMILARITY='0.99 1.0'
```

To detect clones, type 
```
bin/safe cloneDetect 
```
or the following to view the available options and their usages.
```
bin/safe help
```

The available options are as follows:
- **xml**: convert the clone detection results to XML format.
- **function**: detect only function clones.

The clone detection results are available at the directory **build/clusters** with the prefix ``post_cluster_vdb``.

To view the clone reports in XML format in a browser, use [Source-highlight](https://www.gnu.org/software/src-highlite/source-highlight.html) to highlight the source code with the following and open the XML reports in a browser.
```
source-highlight -s js --line-number-ref filename.js
```
