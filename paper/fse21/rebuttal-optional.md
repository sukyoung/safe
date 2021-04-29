--------------------------------------------------------------------------------
# Individual Response (Optional)
--------------------------------------------------------------------------------


Review A
------------

### Abstract

__A1)__ The abstract is too long and meandering

__=>__ TODO

__A2)__ "Sealed state" is a term for your new contribution (I believe), but it
is not understood by the non-expert reader. I would avoid using jargon in the
abstract.

__=>__ TODO

__A3)__ This approach could be explained more intuitively in the abstract.
E.g., "We eliminate the need to analyze some methods by using the concrete
parameters used to call them, and simply executing the code instead of
analyzing it." This is probably not a totally correct characterization of your
work, but it is much more intuitive and easy to understand.

__=>__ TODO

__A4)__ Microcontrollers for IoT is not a major use of Javascript… seems off
topic.

__=>__ TODO

__A5)__ What do you mean by host environments?

__=>__ TODO

__A6)__ What do you mean by "high performance"? Do you mean fast? Do you mean
accurate?

__=>__ TODO

__A7)__ It is difficult to make the jump from the high-level discussion (top)
to the detailed discussion (bottom).

__=>__ TODO


### Section 1 Introduction

__A8)__ I don’t understand the focus on JavaScript in IoT environments… how is
that relevant?

__=>__ TODO

__A9)__ "High performance" is an odd phrase to use here. I would consider
speaking of the attribute you appreciate directly. I think you are referring to
speed.

__=>__ TODO

__A10)__ The characterization of related work makes it difficult to determine
how your work differs from theirs.

__=>__ TODO

__A11)__ I do not follow the example very well. I am not an expert on this type
of program analysis, but I do understand program analysis generally, and I feel
like this example is not clear. I would remove it.

__=>__ TODO

### Section 2 Motivation

__A12)__ These examples are helpful for understanding your approach. I would
remove the other example from the introduction, which was not helpful because
it was too brief.

__=>__ TODO

### Section 6 Evaluation

__A13)__ The communication costs section could be removed.

__=>__ TODO

__A14)__ Section 6.2 Precision Improvement) Can you discuss the practical
implications of removing these failed assertions more clearly?

__=>__ TODO

### Questions for authors

__A15)__ Can you describe the key insight(s) of your approach, as if you were
speaking to a non-expert, in 1-2 sentences?

__=>__ TODO


--------------------------------------------------------------------------------


Review B
------------

### Comments for authors

__B1)__ The idea of combining concrete and symbolic execution is a good one. I
have not seen this done in Abstract Interpretation setting before. However, it
is well established in Symbolic Execution domain. Concolic execution,
especially as pioneered by EXE, and is currently best exemplified by KLEE,
seems the closest to the proposed technique.

__=>__ TODO

__B2)__ Symbolic Execution can be seen as abstract interpretation over an
abstract domain of expressions. From the high-level, it is identical to what is
proposed in the paper: state is divided into concrete and abstract values,
concrete values are executed concretely, symbolic are executed symbolically.
Concrete values can be converted to symbolic, but not the other way around.
There is a large body of related work on all kind of iterations of this basic
idea.

__=>__ TODO

__B3)__ The paper spends a significant amount of space formalizing the
semantics and the abstract domain. The formalism is appreciated, but it belongs
to the appendix rather than the main body of the paper. Nothing new is
communicated by it in addition to what is described in the motivation.

__=>__ TODO

__B4)__ However, what is missing is a clear identification of the limitations
of the approach. How does the proposed scheme integrates with typical abstract
interpretation analyses? For example, I imagine that aggressive use of joins
and convex abstract domains will create so much abstract values that concrete
execution will not be possible. Similar questions apply to widening,
summary/contract computation for handling recursion, etc.

__=>__ TODO

__B5)__ In the current presentation, it seems that the authors consider only
simple non-relational domains that are lifted to a powerset (i.e., join is
union).  If that is the case, the result is essentially identical to symbolic
execution and the degree of novelty compared to concolic execution is low.

__=>__ TODO

### Questions for authors

__B6)__ What is the key distinguishing characteristic of the proposed approach
with Concolic Symbolic Execution

__=>__ TODO

__B7)__ What are the limitations the approach places on abstract domains /
algorithms used with it

__=>__ TODO

__B8)__ How the proposed technique integrates with typical static analysis:
join, widen, efficient fixedpoint computation, etc.

__=>__ TODO


--------------------------------------------------------------------------------


Review C
------------

### Weak Point 1 (W1)

__C1)__ Sect. 4.3 is one of the most interesting sections - the paper is about
JavaScript after all.  However, 4.3 is very short and Sects. 4.1 and 4.2
introduce a lot of definitions which are not really needed to understand Sect.
4.3.  So maybe 4.1 and 4.2 could be less detailed in favor of 4.3.

__=>__ TODO

__C2)__ The paper could discuss why/how (not) dynamic shortcuts can be used for
other languages.

__=>__ TODO

### Weak Point 2 (W2)

__C3)__ Please make explicit what the baseline analyzer is (I suppose standard
SAFE?).

__=>__ TODO

__C4)__ I think that the chosen benchmark set (Lodash tests) is not a good
choice, because even in the abstracted version, test cases are a special kind
of software (threat to external validity).

__=>__ TODO

__C5)__ Moreover, the authors mention that the benchmark set was already used
twice.  But those uses are by similar authors.  So this statement is not
convincing for the acceptance of the benchmark set in the community.

__=>__ TODO

### Section 6.1 Analysis Speed-up

__C6)__ Communication cost is one of your future work suggestions, so more
details could be interesting: The authors could mention what is included in
communication cost.  The state conversions? Or only data transfer between
processes?  In this context it might also be interesting to know how you
implemented the communication.  And then it can get relevant to know how
exactly you measured it.

__=>__ TODO

### Section 6.2 Precision Improvement

__C7)__ How is "average improvement" (solid line) calculated?

__=>__ TODO

__C8)__ I do not understand where I can see the 24 failed assertions in Figure
9a.

__=>__ TODO

__C9)__ Also the percentages: You write "Figure 9(b) shows ... by 12.32%" I do
not understand how to get the numbers from the figure.  Maybe a table is more
appropriate here?

__=>__ TODO

__C10)__ In general, I find the heat-map notation a bit confusing. Maybe one
could explain an example, like "The grey circle at (0,12) means that x
assertions failed with DS, ..."

__=>__ TODO

__C11)__ I also don't understand what the authors are averaging over in
statements like "92.79% on average".  I understand that for each of the 156
tests a failed assertion can be produced or not.  So it's possible to say,
e.g., if half of the failed assertions are removed, the number is reduced by
50%. But why average?

__=>__ TODO
