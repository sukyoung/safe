We sincerely thank the reviewers for their careful reviews and detailed
suggestions for improvement. We will reflect the suggestions in our revision,
and we answer the outstanding comments and questions from the reviewers as
follows:

- TODO

--------------------------------------------------------------------------------
# Individual Response (Optional)
--------------------------------------------------------------------------------


Review #162A
------------

### Abstract

- The abstract is too long and meandering

- "Sealed state" is a term for your new contribution (I believe), but it is not
  understood by the non-expert reader. I would avoid using jargon in the
  abstract.

- This approach could be explained more intuitively in the abstract. E.g., "We
  eliminate the need to analyze some methods by using the concrete parameters
  used to call them, and simply executing the code instead of analyzing it."
  This is probably not a totally correct characterization of your work, but it
  is much more intuitive and easy to understand.

- Microcontrollers for IoT is not a major use of Javascript… seems off topic.

- What do you mean by host environments?

- What do you mean by "high performance"? Do you mean fast? Do you mean
  accurate?

- It is difficult to make the jump from the high-level discussion (top) to the
  detailed discussion (bottom).

### Section 1 Introduction

- I don’t understand the focus on JavaScript in IoT environments… how is that
  relevant?

- "High performance" is an odd phrase to use here. I would consider speaking of
  the attribute you appreciate directly. I think you are referring to speed.

- The characterization of related work makes it difficult to determine how your
  work differs from theirs.

- I do not follow the example very well. I am not an expert on this type of
  program analysis, but I do understand program analysis generally, and I feel
  like this example is not clear. I would remove it.

- The performance gains seem very strong.

### Section 2 Motivation

- These examples are helpful for understanding your approach. I would remove
  the other example from the introduction, which was not helpful because it was
  too brief.

### Section 5 Implementation

- Good description of implementation and clever approach.

### Section 6 Evaluation

- The speedup seems remarkable.

- The communication costs section could be removed.

- Section 6.2 Precision Improvement) Can you discuss the practical implications
  of removing these failed assertions more clearly?

### Questions for authors

- Can you describe the key insight(s) of your approach, as if you were speaking
  to a non-expert, in 1-2 sentences?


--------------------------------------------------------------------------------


Review #162B
------------

### Comments for authors
- The idea of combining concrete and symbolic execution is a good one. I have
  not seen this done in Abstract Interpretation setting before. However, it is
  well established in Symbolic Execution domain. Concolic execution, especially
  as pioneered by EXE, and is currently best exemplified by KLEE, seems the
  closest to the proposed technique.

- Symbolic Execution can be seen as abstract interpretation over an abstract
  domain of expressions. From the high-level, it is identical to what is
  proposed in the paper: state is divided into concrete and abstract values,
  concrete values are executed concretely, symbolic are executed symbolically.
  Concrete values can be converted to symbolic, but not the other way around.
  There is a large body of related work on all kind of iterations of this basic
  idea.

- The paper spends a significant amount of space formalizing the semantics and
  the abstract domain. The formalism is appreciated, but it belongs to the
  appendix rather than the main body of the paper. Nothing new is communicated
  by it in addition to what is described in the motivation.

- However, what is missing is a clear identification of the limitations of the
  approach. How does the proposed scheme integrates with typical abstract
  interpretation analyses? For example, I imagine that aggressive use of joins
  and convex abstract domains will create so much abstract values that concrete
  execution will not be possible. Similar questions apply to widening,
  summary/contract computation for handling recursion, etc.

- In the current presentation, it seems that the authors consider only simple
  non-relational domains that are lifted to a powerset (i.e., join is union).
  If that is the case, the result is essentially identical to symbolic
  execution and the degree of novelty compared to concolic execution is low.

### Questions for authors

- What is the key distinguishing characteristic of the proposed approach with
  Concolic Symbolic Execution

- What are the limitations the approach places on abstract domains / algorithms
  used with it

- How the proposed technique integrates with typical static analysis: join,
  widen, efficient fixedpoint computation, etc.


--------------------------------------------------------------------------------


Review #162C
------------

### Weak Point 1 (W1)

- Sect. 4.3 is one of the most interesting sections - the paper is about
  JavaScript after all.  However, 4.3 is very short and Sects. 4.1 and 4.2
  introduce a lot of definitions which are not really needed to understand
  Sect. 4.3.  So maybe 4.1 and 4.2 could be less detailed in favor of 4.3.

- The paper could discuss why/how (not) dynamic shortcuts can be used for other
  languages.

### Weak Point 2 (W2)

- Please make explicit what the baseline analyzer is (I suppose standard
  SAFE?).

- I think that the chosen benchmark set (Lodash tests) is not a good choice,
  because even in the abstracted version, test cases are a special kind of
  software (threat to external validity).

- Moreover, the authors mention that the benchmark set was already used twice.
  But those uses are by similar authors.  So this statement is not convincing
  for the acceptance of the benchmark set in the community.

### Section 6.1 Analysis Speed-up
- Communication cost is one of your future work suggestions, so more details
  could be interesting: The authors could mention what is included in
  communication cost.  The state conversions? Or only data transfer between
  processes?  In this context it might also be interesting to know how you
  implemented the communication.  And then it can get relevant to know how
  exactly you measured it.

### Section 6.2 Precision Improvement

- How is "average improvement" (solid line) calculated?

- I do not understand where I can see the 24 failed assertions in Figure 9a.

- Also the percentages: You write "Figure 9(b) shows ... by 12.32 %" I do not
  understand how to get the numbers from the figure.  Maybe a table is more
  appropriate here?

- In general, I find the heat-map notation a bit confusing. Maybe one could
  explain an example, like "The grey circle at (0,12) means that x assertions
  failed with DS, ..."

- I also don't understand what the authors are averaging over in statements
  like "92.79 % on average".  I understand that for each of the 156 tests a
  failed assertion can be produced or not.  So it's possible to say, e.g., if
  half of the failed assertions are removed, the number is reduced by 50 %. But
  why average?
