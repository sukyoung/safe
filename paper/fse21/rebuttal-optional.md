--------------------------------------------------------------------------------
# Individual Response (Optional)
--------------------------------------------------------------------------------


Review A
------------

### Abstract

__A1)__ The abstract is too long and meandering

__=>__ Thank you for the detailed comments. We will revise our paper with considering your comments for the next version. We will modify to remove unnecessary details in the abstract.

__A2)__ "Sealed state" is a term for your new contribution (I believe), but it
is not understood by the non-expert reader. I would avoid using jargon in the
abstract.

__=>__ We will rephrase to remove some jargons.

__A3)__ This approach could be explained more intuitively in the abstract.
E.g., "We eliminate the need to analyze some methods by using the concrete
parameters used to call them, and simply executing the code instead of
analyzing it." This is probably not a totally correct characterization of your
work, but it is much more intuitive and easy to understand.

__=>__ We will add an intuitive use case.

__A4)__ Microcontrollers for IoT is not a major use of Javascript… seems off
topic.

__=>__ TODO  Microcontrollers are the emerging field of JavaScript accoring to ...

__A5)__ What do you mean by host environments?

__=>__ Each browser and server that supports ECMAScript supplies its own host environment, completing the ECMAScript execution environment. For instance, a browser supplies objects that represent windows, pop-ups, text areas, cookies, and input/output.

__A6)__ What do you mean by "high performance"? Do you mean fast? Do you mean
accurate?

__=>__ We refer to "short elapsed time" as "high performance" at all time.

__A7)__  It is difficult to make the jump from the high-level discussion (top)
to the detailed discussion (bottom).

__=>__ We will reorganize high-level and detailed discussions.


### Section 1 Introduction

__A8)__ I don’t understand the focus on JavaScript in IoT environments… how is
that relevant?

__=>__ The same answer as A4).

__A9)__ "High performance" is an odd phrase to use here. I would consider
speaking of the attribute you appreciate directly. I think you are referring to
speed.

__=>__ The same answer as A6).

__A10)__ The characterization of related work makes it difficult to determine
how your work differs from theirs.

__=>__ The key differences are 1) preserving the soundness of the static analyzer and 2) the most flexible form of utilizing the dynamic analysis.

__A11)__ I do not follow the example very well. I am not an expert on this type
of program analysis, but I do understand program analysis generally, and I feel
like this example is not clear. I would remove it.

__=>__ We will rephrase explanations more clearly.

### Section 2 Motivation

__A12)__ These examples are helpful for understanding your approach. I would
remove the other example from the introduction, which was not helpful because
it was too brief.

__=>__ The same answer as A11).

### Section 6 Evaluation

__A13)__ The communication costs section could be removed.

__=>__ TODO

__A14)__ Section 6.2 Precision Improvement) Can you discuss the practical
implications of removing these failed assertions more clearly?

__=>__ TODO

### Questions for authors

__A15)__ Can you describe the key insight(s) of your approach, as if you were
speaking to a non-expert, in 1-2 sentences?

__=>__ Briefly speaking, dynamic analyses on commercial engines are much faster than static analyzers, so we can accelerate the static analysis as much as we substitute some parts of the static analysis to the dynamic analysis.
We propose a novel technique to utilize the dynamic analysis during the static analysis with the most flexible form and in a sound manner.


--------------------------------------------------------------------------------


Review B
------------

### Comments for authors

__B1)__ The idea of combining concrete and symbolic execution is a good one. I
have not seen this done in Abstract Interpretation setting before. However, it
is well established in Symbolic Execution domain. Concolic execution,
especially as pioneered by EXE, and is currently best exemplified by KLEE,
seems the closest to the proposed technique.

__=>__ COMPARISON WITH CONCOLIC TESTING

__B2)__ Symbolic Execution can be seen as abstract interpretation over an
abstract domain of expressions. From the high-level, it is identical to what is
proposed in the paper: state is divided into concrete and abstract values,
concrete values are executed concretely, symbolic are executed symbolically.
Concrete values can be converted to symbolic, but not the other way around.
There is a large body of related work on all kind of iterations of this basic
idea.

__=>__ COMPARISON WITH CONCOLIC TESTING

__B3)__ The paper spends a significant amount of space formalizing the
semantics and the abstract domain. The formalism is appreciated, but it belongs
to the appendix rather than the main body of the paper. Nothing new is
communicated by it in addition to what is described in the motivation.

__=>__ IMPORTANCE OF FORMALIZATION

__B4)__ However, what is missing is a clear identification of the limitations
of the approach. How does the proposed scheme integrates with typical abstract
interpretation analyses? For example, I imagine that aggressive use of joins
and convex abstract domains will create so much abstract values that concrete
execution will not be possible. Similar questions apply to widening,
summary/contract computation for handling recursion, etc.

__=>__ Although our combined analysis is compatible for relational abstract domains or domain opertaions like widening, it may not have practical benefits for those cases.
If the number of abstract values increases, the chance of concrete execution decreases.
So overhead from failures of state conversion and concrete executions may the exceed benefits from success of concrete executions in theory.
We need future studies about such trade-offs with various domains and programs to answer the question.

__B5)__ In the current presentation, it seems that the authors consider only
simple non-relational domains that are lifted to a powerset (i.e., join is
union).  If that is the case, the result is essentially identical to symbolic
execution and the degree of novelty compared to concolic execution is low.

__=>__ The same answer as B4).

### Questions for authors

__B6)__ What is the key distinguishing characteristic of the proposed approach
with Concolic Symbolic Execution

__=>__ COMPARISON WITH CONCOLIC TESTING

__B7)__ What are the limitations the approach places on abstract domains /
algorithms used with it

__=>__ The same answer as B4).

__B8)__ How the proposed technique integrates with typical static analysis:
join, widen, efficient fixedpoint computation, etc.

__=>__ The same answer as B4).


--------------------------------------------------------------------------------


Review C
------------

### Weak Point 1 (W1)

__C1)__ Sect. 4.3 is one of the most interesting sections - the paper is about
JavaScript after all.  However, 4.3 is very short and Sects. 4.1 and 4.2
introduce a lot of definitions which are not really needed to understand Sect.
4.3.  So maybe 4.1 and 4.2 could be less detailed in favor of 4.3.

__=>__ We will balance texts in subsections of Section 4.

__C2)__ The paper could discuss why/how (not) dynamic shortcuts can be used for
other languages.

__=>__ Dynamic shortcuts can be applicable for other languages, in theory, however, we think the following conditions required to get practical benefits.
- The dynamic analysis on a concrete engine should be much faster than the static analysis.
- The target program contains deterministic parts that the dynamic analysis is going to be used.
JavaScript that we firstly targeted satisfies the conditions and motivated us.
But we need future studies about other languages to discuss concretely.


### Weak Point 2 (W2)

__C3)__ Please make explicit what the baseline analyzer is (I suppose standard
SAFE?).

__=>__ We used the baseline analyzer based on the standard SAFE with minor changes.
1) We increase its sensitivity options of "callsite" from 20 to 30 and "loopIter" 100 to 400 from the default options.
2) We modified some incomplete models for opaque codes to analyze Lodash tests soundly.

__C4)__ I think that the chosen benchmark set (Lodash tests) is not a good
choice, because even in the abstracted version, test cases are a special kind
of software (threat to external validity).

__=>__ The same answer as C5) 

__C5)__ Moreover, the authors mention that the benchmark set was already used
twice.  But those uses are by similar authors.  So this statement is not
convincing for the acceptance of the benchmark set in the community.

__=>__ Most static analysis techniques for JavaScript programs have been evaluated with
famous JavaScript libraries. Until the mid-2010s, most of the researchers
[[ECOOP'15](https://drops.dagstuhl.de/opus/volltexte/2015/5245/),
[DLS'16](https://dl.acm.org/doi/10.1145/2989225.2989228),
[APLAS'17](https://link.springer.com/chapter/10.1007/978-3-319-71237-6_8)]
focused on jQuery and its benchmarks.  However, recent techniques
[[OOPSLA'19](https://dl.acm.org/doi/abs/10.1145/3360566),
[ECOOP'20](https://drops.dagstuhl.de/opus/volltexte/2020/13173/)] are evaluated
with Lodash and its official tests because of its popularity and complex field
copy patterns (e.g. `mixin` function).   We agree that real-world benchmarks
are more interesting and appropriate to evaluate dynamic shortcuts than
randomly _abstracted_ Lodash 4 tests.  However, static analysis of real-world
JavaScript applications require diverse host-dependent abstract models such as
DOM trees/APIs and event handlers for web applications or file systems,
networks, and module imports for Node.js applications.  Thus, we decided to
focus on pure JavaScript applications and to randomly abstract Lodash 4 tests.

### Section 6.1 Analysis Speed-up

__C6)__ Communication cost is one of your future work suggestions, so more
details could be interesting: The authors could mention what is included in
communication cost.  The state conversions? Or only data transfer between
processes?  In this context it might also be interesting to know how you
implemented the communication.  And then it can get relevant to know how
exactly you measured it.

__=>__ The current implementation we used the SAFE analyzer written in Scala (tool) and the dynamic analysis naturally runs on JavaScript engine.
They run as two different processes and communicate through a localhost server.
So the communication cost consist of both the state conversions and data transfer.
We measured state conversions and data transfer separately but we simply present the sum of them because they are almost proportional.

### Section 6.2 Precision Improvement

__C7)__ How is "average improvement" (solid line) calculated?

__=>__ For each test case except on (0, 0), we calcuate the slope by ((the number of failed assetions from DS) / (the number of failed assetions analyzed from no-DS)) and calcuate average of them.

__C8)__ I do not understand where I can see the 24 failed assertions in Figure
9a.

__=>__ We tried to say the DS did not produce any failed assertions for 24 test cases that the no-DS produced at least 2 failed assertions for.
The darker the circle is, the more tests it indicates.
Thus, the sum of test cases from the circles on the x-axis except (0, 0) is 24.

__C9)__ Also the percentages: You write "Figure 9(b) shows ... by 12.32%" I do
not understand how to get the numbers from the figure.  Maybe a table is more
appropriate here?

__=>__ The improvement of "12.31%" can be calculated by (1 - (the slope of the solid line)).

__C10)__ In general, I find the heat-map notation a bit confusing. Maybe one
could explain an example, like "The grey circle at (0,12) means that x
assertions failed with DS, ..."

__=>__ We will add an example, as you commented.

__C11)__ I also don't understand what the authors are averaging over in
statements like "92.79% on average".  I understand that for each of the 156
tests a failed assertion can be produced or not.  So it's possible to say,
e.g., if half of the failed assertions are removed, the number is reduced by
50%. But why average?

__=>__ We will revise the whole paragraph to explain Figure 9 more clearly.
