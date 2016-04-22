SAFE
====

### News

SAFE is now available at GitHub.  Older versions are still available at:

    http://safe.kaist.ac.kr

but note that we use **sbt** instead of **ant** to build SAFE, which required slight restructuring of source directories.

### Introduction

SAFE is a scalable and pluggable analysis framework for JavaScript web applications developed by the Programming Language Research Group at KAIST:

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

  * J2SDK 1.7.  See http://java.sun.com/javase/downloads/index.jsp
  * sbt version 0.13 or later.  See http://www.scala-sbt.org
  * Bash version 2.5 or later, installed at /bin/bash.  See http://www.gnu.org/software/bash/
  * xtc, copied as $JS_HOME/bin/xtc.jar.  See http://cs.nyu.edu/rgrimm/xtc/

In your shell startup script, add $JS_HOME/bin to your path.  The shell scripts in this directory are Bash scripts.  To run them, you must have Bash accessible in /bin/bash.

### Installation

After launching sbt, type **antRun clean compile** and then **compile**.

Once you have built the framework, you can call it from any directory, on any JavaScript file, simply by typing one of available commands at a command line.  You can see the available commands by typing:

    bin/jsaf
    bin/jsaf help

### Run Tests

Still inside sbt, type **antRun test**.

### CRES: Clone Refactor for ECMAScript

====

CRES is an open-source clone refactoring plugin for Eclipse.

### Installation

1. Install the following using Eclipse's update manager:
 * Eclipse 4 Core Tools. [Update Site] [e4]
 * JavaScript Development Tools. [Update Site] [JSDT]
2. Copy the file **kr.ac.kaist.jsaf.clone\_refactor\_[osx|linux].jar**
in the [dist] [disturl] directory to the **plugins** directory of Eclipse
3. Restart Eclipse

[e4]:
http://download.eclipse.org/e4/downloads/drops/S-0.17-201501051100/repository/
[JSDT]: http://download.eclipse.org/webtools/repository/luna/
[disturl]: https://github.com/sukyoung/safe/tree/master/dist

### Usage

1. Launch the plugin from the menu or toolbar of Eclipse.
2. From the **Configuration Dialog**, select a project from the workspace to be analyzed and configure the minimum size of clones.
3. From the **Clone View**,
 * Double click any fragments to view the duplicated portion in the text editor.
 * Select two duplicated fragments from the same group and compare their differences using **Compare** from the toolbar.
 * Select at least two duplicated fragments from the same group and view the refactoring suggestions using **Pull Up Method** from the toolbar.

