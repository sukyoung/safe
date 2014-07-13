SAFE
====

### News

SAFE is now available at GitHub.  Older versions are still available at:

    http://safe.kaist.ac.kr

but note that we use **sbt** instead of **ant** to build SAFE, which required slight restructuring of source directories.

### Introduction

SAFE is a scalable and pluggable analysis frameowkr for JavaScript web applications developed by the Programming Language Research Group at KAIST:

    http://plrg.kaist.ac.kr

We provide a formal specification of the SAFE framework:

    http://plrg.kaist.ac.kr/redmine/projects/jsf/repository/revisions/master/show/doc/manual
    
and our papers on SAFE are available at:

  * http://plrg.kaist.ac.kr/_media/research/publications/fool2012.pdf
  * http://plrg.kaist.ac.kr/_media/research/publications/oopsla12.pdf
  * http://plrg.kaist.ac.kr/_media/research/publications/dls13.pdf
  * http://plrg.kaist.ac.kr/_media/research/publications/modularity14.pdf
  * http://plrg.kaist.ac.kr/_media/research/publications/fse14.pdf

Our academic colleagues using SAFE are:

  * http://rosaec.snu.ac.kr @ Seoul National University
  * http://www.kframework.org/index.php/Main_Page @ University of Illinois at Urbana-Champaign
  * http://www.cse.ust.hk/~hunkim/ @ HKUST

and our project has been supported by:

  * Korea Ministry of Education, Science and Technology(MEST)
  * National Research Foundation of Korea(NRF)
  * Samsung Electronics
  * S-Core., Ltd.
  * Google
  * Microsoft Research Asia

### Requirements

We assume you are using an operating system with a Unix-style shell (for example, Mac OS X, Linux, or Cygwin on Windows).  Assuming **JS_HOME** points to the SAFE directory, you will need to have access to the following:

  * J2SDK 1.7 or later.  See http://java.sun.com/javase/downloads/index.jsp
  * sbt version 0.13 or later.  See http://www.scala-sbt.org
  * Bash version 2.5 or later, installed at /bin/bash.  See http://www.gnu.org/software/bash/
  * xtc, copied as $JS_HOME/bin/xtc.jar.  See http://cs.nyu.edu/rgrimm/xtc/

In your shell startup script, add $JS_HOME/bin to your path.  The shell scripts in this directory are Bash scripts.  To run them, you must have Bash accessible in /bin/bash.

### Installation

After launching sbt, type **antRun compile** and then **compile**.

Once you have built the framework, you can call it from any directory, on any JavaScript file, simply by typing one of available commands at a command line.  You can see the available commands by typing:

    bin/jsaf
    bin/jsaf help

### Run Tests

Still inside sbt, type **antRun test**.

