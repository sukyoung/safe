We sincerely thank the reviewers for their careful reviews and detailed
suggestions for improvement. We will reflect the suggestions in our revision,
and we answer the outstanding comments and questions from the reviewers as
follows:

### Novelty (Review A, B)

The most important contribution of this paper is to present the __first
formalization of _flexible_ switching between abstract and concrete execution
for JavaScript in a sound way__.  We introduced a _dynamic shortcut_ as a
technique to perform concrete execution with abstract values during static
analysis to enhance analysis performance and precision.  To be aware of when the
actual value of each abstract value is required in the concrete execution, we
converted it to a _sealed value_, which is a symbol that signals the end of the
current dynamic shortcut when the concrete execution tries to access its value.
The most related previous work is Concerto
[[POPL'19](https://dl.acm.org/doi/abs/10.1145/3290356)], a Java static analyzer
with mostly-concrete execution described in the related work section.  Similar
to our dynamic shortcuts, Concerto combines static analysis with extended
concrete execution (mostly-concrete execution or symbolic execution) to improve
analysis precision and performance.  However, Concerto has a critical limitation
that they _syntactically_ divide programs into two different parts for static
analysis and concrete execution.  Unlike their approaches, we wanted to utilize
JavaScript engines more aggressively and flexibly by automatically detecting
whether concrete execution is possible without any syntactic restrictions.


### Comparison with Concolic Execution (Review B)

We agree with Review B that _concolic execution_ also has similar concept to
dynamic shortcuts because it also leverages concrete execution not for abstract
interpretation but for _symbolic execution_.  As Review B already mentioned,
symbolic execution can be treated as an abstract interpretation with symbolic
expressions and path constraints.  However, we formalized a dynamic shortcut as
a technique to combine concrete execution with general abstract interpretation
not symbolic execution.  Thus, dynamic shortcuts are theoretically applicable
for any type of abstract interpretation including symbolic execution and it is
more general definition of concolic execution.  Nevertheless, we agree that
concolic execution is also closely related to our technique.  We will
supplement citations for symbolic and concolic execution and compare them to
dynamic shortcuts.


### Importance of Formalization (Review B)

The concept of dynamic shortcut is quite simple because it just utilizes
concrete execution with abstract values until their actual values are required.
However, we believe that the formalization of dynamic shortcut is necessary to
prove _soundness_ and _termination_, thus we explained the brief formalization
idea should be explained in the paper.  Although many real-world static
analyzers aim _soundy_ analysis by sacrificing soundness for practical reasons,
it is still important to prove the _soundness_ and _termination_ for a new
analysis technique in theory.  Thus, we formally defined dynamic shortcuts,
found the conditions when the dynamic shortcut would be sound and terminate,
and formally proved them.  We believe that this formalization could answer an
important question: "Are the soundness and termination of the static analysis
with dynamic shortcuts still guaranteed even though parts of abstract
interpretation are replaced with _concrete execution_?''


### Limitation of Dynamic Shortcuts (Review B)

We agree with Review B that the quality of dynamic shortcuts depends on the
shape of the abstract domain including its abstract values and join (widening)
operator.  Although dynamic shortcut is theoretically applicable to any
abstract domain, its quality is not always guaranteed.  If an abstract domain
gives fewer opportunities to utilize concrete execution, the effect of dynamic
shortcuts decrease.  On the other hand, aggressively many dynamic shortcuts
might suffer from massive communication costs.  Although we did not discuss
a more detailed relationship between dynamic shortcuts and the shape of abstract
domain in this paper, we experimentally showed that dynamic shortcuts can
enhance the performance and precision of JavaScript static analysis.  We believe
that finding a more detailed relationship between them is one of good future
research directions.


### Extensibility for Other Languages (Review C)

We believe that dynamic shortcuts are applicable for static analysis of other
languages.  Although the abstract domain and sealed execution in other languages
are totally different from those of JavaScript, the general formalization of
dynamic shortcuts is not dependent on JavaScript language characteristics.  Thus,
it is possible to define dynamic shortcuts on any other languages and they can
enhance the analysis precision.  However, the precision improvement might not be
remarkable or the analysis might suffer from massive communication costs.  We
believe that applying dynamic shortcuts for other languages is a promising
future research direction for dynamic shortcuts.

--------------------------------------------------------------------------------
# Individual Response (Optional)
--------------------------------------------------------------------------------


Review A
------------

### Abstract

__A1) The abstract is too long and meandering__

__=>__ Thank you for the comment. We will remove unnecessary
details in the abstract in the final version of our paper.

__A2) "Sealed state" is a term for your new contribution (I believe), but it
is not understood by the non-expert reader. I would avoid using jargon in the
abstract.__

__=>__ We will rephrase the abstract to remove jargon uses.

__A3) This approach could be explained more intuitively in the abstract.
E.g., "We eliminate the need to analyze some methods by using the concrete
parameters used to call them, and simply executing the code instead of
analyzing it." This is probably not a totally correct characterization of your
work, but it is much more intuitive and easy to understand.__

__=>__ As explained in the __Novelty__ part, we believe that the most important
contribution of this paper is to present the __first formalization of
_flexible_ switching between abstract and concrete execution for JavaScript in
a sound way__.  We will add this sentence to the abstract.

__A4) Microcontrollers for IoT is not a major use of JavaScript… seems off
topic.__

__=>__ We believe that micro-controllers are emerging fields of JavaScript.
A paper [[HOPL'19](https://dl.acm.org/doi/abs/10.1145/3386327)] about the
history of JavaScript also says that:

In addition to server applications built using Node.js and other hosts,
JavaScript is being used to build desktop applications, mobile device
applications, fitness trackers, robots, and numerous embedded systems.

__A5) What do you mean by host environments?__

__=>__ ECMAScript 2020 (ES11) explains host environments as follows:

A host environment typically includes objects or functions which allow
obtaining input and providing output as host-defined properties of the global
object.

For instance, a browser supplies objects that represent windows, pop-ups, text
areas, cookies, and input/output as its host environment.

__A6) What do you mean by "high performance"? Do you mean fast? Do you mean
accurate?__

__=>__ We refer to "short elapsed time" as "high performance" at all times.

__A7)  It is difficult to make the jump from the high-level discussion (top)
to the detailed discussion (bottom).__

__=>__ We will reorganize the abstract in a top-down manner.


### Section 1 Introduction

__A8) I don’t understand the focus on JavaScript in IoT environments… how is
that relevant?__

__=>__ The same answer as A4).

__A9) "High performance" is an odd phrase to use here. I would consider
speaking of the attribute you appreciate directly. I think you are referring to
speed.__

__=>__ The same answer as A6).

__A10) The characterization of related work makes it difficult to determine
how your work differs from theirs.__

__=>__ Please see __Novelty__.

__A11) I do not follow the example very well. I am not an expert on this type
of program analysis, but I do understand program analysis generally, and I feel
like this example is not clear. I would remove it.__

__=>__ We believe that this example explains the core idea of our approach.  We
will revise the explanations more clearly.

### Section 2 Motivation

__A12) These examples are helpful for understanding your approach. I would
remove the other example from the introduction, which was not helpful because
it was too brief.__

__=>__ The same answer as A11).

### Section 6 Evaluation

__A13) The communication costs section could be removed.__

__=>__ We believe that reducing the communication cost is also an important
problem because the analysis might suffer from large communication costs.

__A14) Section 6.2 Precision Improvement) Can you discuss the practical
implications of removing these failed assertions more clearly?__

__=>__ In static analysis, counting the number of failed assertions is a
typical way to measure analysis precision.  More failed assertions denote
less precise analysis.  Thus, we experimentally showed that dynamic shortcuts
can improve the precision of JavaScript static analysis.

### Questions for authors

__A15) Can you describe the key insight(s) of your approach, as if you were
speaking to a non-expert, in 1-2 sentences?__

__=>__ Briefly speaking, because dynamic analysis on a commercial engine is much faster
than static analysis, we can accelerate static analysis by substituting
some parts of the static analysis with dynamic analysis.  We propose a novel
technique to utilize dynamic analysis during static analysis with a
flexible and sound manner.


--------------------------------------------------------------------------------


Review B
------------

### Comments for authors

__B1) The idea of combining concrete and symbolic execution is a good one. I
have not seen this done in Abstract Interpretation setting before. However, it
is well established in Symbolic Execution domain. Concolic execution,
especially as pioneered by EXE, and is currently best exemplified by KLEE,
seems the closest to the proposed technique.__

__=>__ Thank you for the constructive comments about concolic execution.
Please see __Comparison with Concolic Execution__.

__B2) Symbolic Execution can be seen as abstract interpretation over an
abstract domain of expressions. From the high-level, it is identical to what is
proposed in the paper: state is divided into concrete and abstract values,
concrete values are executed concretely, symbolic are executed symbolically.
Concrete values can be converted to symbolic, but not the other way around.
There is a large body of related work on all kind of iterations of this basic
idea.__

__=>__ Please see __Comparison with Concolic Execution__.

__B3) The paper spends a significant amount of space formalizing the
semantics and the abstract domain. The formalism is appreciated, but it belongs
to the appendix rather than the main body of the paper. Nothing new is
communicated by it in addition to what is described in the motivation.__

__=>__ Please see __Importance of Formalization__.

__B4) However, what is missing is a clear identification of the limitations
of the approach. How does the proposed scheme integrates with typical abstract
interpretation analyses? For example, I imagine that aggressive use of joins
and convex abstract domains will create so much abstract values that concrete
execution will not be possible. Similar questions apply to widening,
summary/contract computation for handling recursion, etc.__

__=>__ Please see __Limitation of Dynamic Shortcuts__.

__B5) In the current presentation, it seems that the authors consider only
simple non-relational domains that are lifted to a powerset (i.e., join is
union).  If that is the case, the result is essentially identical to symbolic
execution and the degree of novelty compared to concolic execution is low.__

__=>__ Please see __Limitation of Dynamic Shortcuts__.

### Questions for authors

__B6) What is the key distinguishing characteristic of the proposed approach
with Concolic Symbolic Execution__

__=>__ Please see __Comparison with Concolic Execution__.

__B7) What are the limitations the approach places on abstract domains /
algorithms used with it__

__=>__ Please see __Limitation of Dynamic Shortcuts__.

__B8) How the proposed technique integrates with typical static analysis:
join, widen, efficient fixedpoint computation, etc.__

__=>__ Please see __Limitation of Dynamic Shortcuts__.


--------------------------------------------------------------------------------


Review C
------------

### Weak Point 1 (W1)

__C1) Sect. 4.3 is one of the most interesting sections - the paper is about
JavaScript after all.  However, 4.3 is very short and Sects. 4.1 and 4.2
introduce a lot of definitions which are not really needed to understand Sect.
4.3.  So maybe 4.1 and 4.2 could be less detailed in favor of 4.3.__

__=>__ While Section 4.2 extends concrete execution of JavaScript explained in
Section 4.1 to abstract execution, Section 4.3 extends it to sealed execution.
Thus, we believe that Section 4.1 is necessary to understand the baseline of
sealed execution. Moreover, Section 4.2 is not directly related to Section 4.3
but Section 4.2 itself is important to define how to abstract the concrete
semantics of JavaScript.

__C2) The paper could discuss why/how (not) dynamic shortcuts can be used for
other languages.__

__=>__ Thank you for a good question about the extensibility of dynamic shortcuts for other languages.  Please see __Extensibility for Other Languages__.


### Weak Point 2 (W2)

__C3) Please make explicit what the baseline analyzer is (I suppose standard
SAFE?).__

__=>__ The baseline analyzer is the standard SAFE with the
following small changes.  We will explain this configuration in the Evaluation
section.

1. We increased the call-site sensitivity from 20 to 30 and the iteration of
loop sensitivity from 100 to 400.
2. We modified some incomplete models for opaque functions to analyze Lodash
tests soundly.

__C4) I think that the chosen benchmark set (Lodash tests) is not a good
choice, because even in the abstracted version, test cases are a special kind
of software (threat to external validity).__

__=>__ Most static analysis techniques for JavaScript programs have been
evaluated with famous JavaScript libraries. Until the mid-2010s, most of the
researchers [[ECOOP'15](https://drops.dagstuhl.de/opus/volltexte/2015/5245/),
[DLS'16](https://dl.acm.org/doi/10.1145/2989225.2989228),
[APLAS'17](https://link.springer.com/chapter/10.1007/978-3-319-71237-6_8)]
focused on jQuery and its benchmarks.  However, recent techniques
[[OOPSLA'19](https://dl.acm.org/doi/abs/10.1145/3360566),
[ECOOP'20](https://drops.dagstuhl.de/opus/volltexte/2020/13173/)] are evaluated
with Lodash and its official tests because of its popularity and complex field
copy patterns (e.g. `mixin` function).   We agree that real-world benchmarks
are more interesting and appropriate to evaluate dynamic shortcuts than
randomly _abstracted_ Lodash 4 tests.  However, static analysis of real-world
JavaScript applications requires diverse host-dependent abstract models such as
DOM trees/APIs and event handlers for web applications or file systems,
networks, and module imports for Node.js applications.  Thus, we decided to
focus on pure JavaScript applications and to randomly abstract Lodash 4 tests.

__C5) Moreover, the authors mention that the benchmark set was already used
twice.  But those uses are by similar authors.  So this statement is not
convincing for the acceptance of the benchmark set in the community.__

__=>__ The same answer as C4)

### Section 6.1 Analysis Speed-up

__C6) Communication cost is one of your future work suggestions, so more
details could be interesting: The authors could mention what is included in
communication cost.  The state conversions? Or only data transfer between
processes?  In this context it might also be interesting to know how you
implemented the communication.  And then it can get relevant to know how
exactly you measured it.__

__=>__ In the current implementation, we used the SAFE analyzer written in
Scala, and the dynamic analysis naturally runs on a JavaScript engine. They run
as two different processes and communicate through a localhost server.  So the
communication cost consists of state conversions and data transfers.  We
measured state conversions and data transfers separately, but we simply
presented their sum because they are almost proportional.

### Section 6.2 Precision Improvement

Thank you for pointing out some misleading explanations about the precision
improvement.  We will answer all the questions and revise this section based on
these answers.

__C7) How is "average improvement" (solid line) calculated?__

__=>__  Each Lodash 4 test contains multiple assertions and it is represented
as a circle in heat-map charts.  Since circles are positioned in the same point
when their corresponding tests have the same number of failed assertions, we
use darker gray to denote a larger number of tests.  For each test case except
on (0, 0), we calculate the slope by ((the number of failed assertions from DS)
/ (the number of failed assertions from no-DS)) and calculate average of them.

__C8) I do not understand where I can see the 24 failed assertions in Figure__
9a.

__=>__ We wanted to say that DS did not produce any failed assertions for 24
test cases that no-DS produced at least 2 failed assertions for.  The darker
circle is, the more tests it indicates.  Thus, the sum of the test cases from
the circles on the x-axis except (0, 0) is 24.

__C9) Also the percentages: You write "Figure 9(b) shows ... by 12.32%" I do
not understand how to get the numbers from the figure.  Maybe a table is more
appropriate here?__

__=>__ The improvement of "12.31%" can be calculated by (1 - (the slope of the
solid line)).

__C10) In general, I find the heat-map notation a bit confusing. Maybe one
could explain an example, like "The gray circle at (0,12) means that x
assertions failed with DS, ..."__

__=>__ We will add an example as you suggested.

__C11) I also don't understand what the authors are averaging over in
statements like "92.79% on average".  I understand that for each of the 156
tests a failed assertion can be produced or not.  So it's possible to say,
e.g., if half of the failed assertions are removed, the number is reduced by
50%. But why average?__

__=>__ We will revise the whole paragraph to explain Figure 9 more clearly.
