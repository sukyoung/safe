Scalable Analysis Framework for ECMAScript (SAFE) Version 2.0
===========

Introduction
============

SAFE 2.0 is a scalable and pluggable analysis frameowkr for JavaScript web applications developed by the Programming Language Research Group at KAIST:

    http://plrg.kaist.ac.kr

Older versions are still available at the safe1.0 branch.

We provide `a formal specification`_ of the SAFE framework
and our papers on SAFE are available:

  * `Battles with False Positives in Static Analysis of JavaScript Web Applications in the Wild`_ (ICSE SEIP 2016)
  * `Static Analysis of JavaScript Web Applications in the Wild via Practical DOM Modeling`_ (ASE 2015)
  * `Practically Tunable Static Analysis Framework for Large-Scale JavaScript Applications`_ (ASE 2015)
  * `Development Nature Matters: An Empirical Study of Code Clones in JavaScript Applications`_ (EMSE 2015)
  * `Scalable and Precise Static Analysis of JavaScript Applications via Loop-Sensitivity`_ (ECOOP 2015)
  * `SAFE_WAPI: Web API Misuse Detector for Web Applications`_ (FSE 2014)
  * `All about the ''with'' Statement in JavaScript: Removing ''with'' Statements in JavaScript Applications`_ (DLS 2013)
  * `Formal Specification of a JavaScript Module System`_ (OOPSLA 2012)
  * `SAFE: Formal Specification and Implementation of a Scalable Analysis Framework for ECMAScript`_ (FOOL 2012)

.. _Battles with False Positives in Static Analysis of JavaScript Web Applications in the Wild: http://plrg.kaist.ac.kr/lib/exe/fetch.php?media=research:publications:icse-seip16.pdf
.. _Static Analysis of JavaScript Web Applications in the Wild via Practical DOM Modeling: http://plrg.kaist.ac.kr/lib/exe/fetch.php?media=research:publications:ase15dom.pdf
.. _Practically Tunable Static Analysis Framework for Large-Scale JavaScript Applications: http://plrg.kaist.ac.kr/lib/exe/fetch.php?media=research:publications:ase15sparse.pdf
.. _Development Nature Matters: An Empirical Study of Code Clones in JavaScript Applications: http://plrg.kaist.ac.kr/lib/exe/fetch.php?media=research:publications:emse15.pdf
.. _Scalable and Precise Static Analysis of JavaScript Applications via Loop-Sensitivity: http://plrg.kaist.ac.kr/lib/exe/fetch.php?media=research:publications:ecoop15.pdf
.. _SAFE_WAPI: Web API Misuse Detector for Web Applications: http://plrg.kaist.ac.kr/lib/exe/fetch.php?media=research:publications:fse14final.pdf
.. _All about the ''with'' Statement in JavaScript: Removing ''with'' Statements in JavaScript Applications: http://plrg.kaist.ac.kr/lib/exe/fetch.php?media=research:publications:dls13.pdf
.. _Formal Specification of a JavaScript Module System: http://plrg.kaist.ac.kr/lib/exe/fetch.php?media=research:publications:oopsla12.pdf
.. _SAFE: Formal Specification and Implementation of a Scalable Analysis Framework for ECMAScript: http://plrg.kaist.ac.kr/lib/exe/fetch.php?media=research:publications:fool2012.pdf

Projects and colleagues using SAFE include:

  * `ROSAEC`_ @ Seoul National University
  * `K Framework`_ @ University of Illinois at Urbana-Champaign
  * `Ken Cheung`_ @ HKUST
  * `Web-based Vulnerability Detection`_ @ Oracle Labs
  * `Tizen`_ @ Linux Foundation

.. _ROSAEC: http://rosaec.snu.ac.kr
.. _K Framework: http://www.kframework.org/index.php/Main_Page
.. _Ken Cheung: http://www.cse.ust.hk/~hunkim
.. _Web-based Vulnerability Detection: https://labs.oracle.com/pls/apex/f?p=labs:49:::::P49_PROJECT_ID:133
.. _Tizen: https://www.tizen.org

and our project has been supported by:

  * Korea Ministry of Education, Science and Technology (MEST)
  * National Research Foundation of Korea (NRF)
  * Samsung Electronics
  * S-Core., Ltd.
  * Google
  * Microsoft Research Asia

.. _a formal specification: http://plrg.kaist.ac.kr/redmine/projects/jsf/repository/revisions/master/show/doc/manual

### Requirements

We assume you are using an operating system with a Unix-style shell (for example, Mac OS X, Linux, or Cygwin on Windows).
Assuming **SAFE_HOME** points to the SAFE directory, you will need to have access to the following:

  * J2SDK 1.7.  See http://java.sun.com/javase/downloads/index.jsp
  * sbt version 0.13 or later.  See http://www.scala-sbt.org
  * Bash version 2.5 or later, installed at /bin/bash.  See http://www.gnu.org/software/bash/
  * xtc, copied as $SAFE_HOME/lib/xtc.jar.  See http://cs.nyu.edu/rgrimm/xtc/

In your shell startup script, add $SAFE_HOME/bin to your path.  The shell scripts in this directory are Bash scripts.  To run them, you must have Bash accessible in /bin/bash.

### Installation

Type **sbt compile** and then **sbt test** to make sure that your installation successfully finishes the tests.

Once you have built the framework, you can call it from any directory, on any JavaScript file, simply by typing one of available commands at a command line.  You can see the available commands by typing:

    bin/safe
    bin/safe help
